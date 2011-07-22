package org.projectodd.stilts.stomp.client.js;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.ScriptableObject;

public class JavascriptTestRunner extends Runner {

    private TestClass testClass;
    private Context context;
    private ScriptableObject scope;
    private ScriptableObject window;
    private Description description;
    private JSpec jspec;

    public JavascriptTestRunner(Class<? extends JavascriptTestCase> testClass) throws Exception {
        this.testClass = new TestClass( testClass );
        System.err.println( "simpleName = " + getScriptName() );
        setUpRhino();
        loadTests();
    }

    void setUpRhino() throws Exception {
        this.context = Context.enter();
        this.scope = this.context.initStandardObjects();
        // prepare websocket support;
        this.window = (ScriptableObject) this.context.evaluateString( this.scope, "var window = {}; window;", "<cmd>", 1, null );

        this.jspec = new JSpec( this.context, this.scope, this.window );
        this.window.put( "jspec", this.window, this.jspec );
        evaluateResource( "jspec.js" );
    }

    Object evaluateResource(String path) throws IOException {
        return this.jspec.load( path );
    }

    String getScriptName() {
        String name = this.testClass.getJavaClass().getSimpleName();
        name = name.replaceAll( "([A-Z])", "_$1" );
        name = name.toLowerCase();
        name = name.replaceAll( "^_+", "" );
        name = name + ".js";
        return name;
    }
    void loadTests() throws Exception {
        evaluateResource( getScriptName() );
        setUpDescription();
    }

    @Override
    public Description getDescription() {
        return this.description;
    }

    void setUpDescription() {
        this.description = Description.createSuiteDescription( this.testClass.getJavaClass().getSimpleName() );
        NativeArray result = (NativeArray) this.window.get( "tests", this.window );
        long len = result.getLength();

        for (int i = 0; i < len; ++i) {
            ScriptableObject test = (ScriptableObject) result.get( i, result );
            String testName = (String) test.get( "description", test );
            Description child = Description.createTestDescription( this.testClass.getJavaClass(), testName );
            description.addChild( child );
        }
    }

    @Override
    public void run(RunNotifier notifier) {
        System.err.println( notifier );
        int i = 0;
        NativeArray tests = (NativeArray) this.window.get( "tests", this.window );

        for (Description testDescription : this.description.getChildren()) {
            ScriptableObject test = (ScriptableObject) tests.get( i, tests );
            Function body = (Function) test.get( "body", test );
            Context.enter();
            try {
                Object testObj = this.testClass.getJavaClass().newInstance();
                notifier.fireTestStarted( testDescription );
                List<FrameworkMethod> befores = this.testClass.getAnnotatedMethods( Before.class );
                for (FrameworkMethod each : befores) {
                    each.invokeExplosively( testObj, new Object[] {} );
                }
                
                List<FrameworkField> exposed = this.testClass.getAnnotatedFields( Expose.class );
                for ( FrameworkField each : exposed ) {
                    String name = each.getField().getName();
                    Object value = each.getField().get( testObj );
                    body.put( name, body.getParentScope(), value );
                }
                body.call( context, body, body, new Object[] {} );
            } catch (AssertionError e) {
                notifier.fireTestFailure( new Failure( testDescription, e ) );
            } catch (Throwable e) {
                notifier.fireTestFailure( new Failure( testDescription, e ) );
            } finally {
                Context.exit();
                notifier.fireTestFinished( testDescription );
            }
            ++i;
        }
    }
}
