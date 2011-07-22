package org.projectodd.stilts.stomp.client.js;

import java.io.IOException;
import java.util.List;

import org.junit.After;
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

    public JavascriptTestRunner(Class<?> testClass) throws Exception {
        this.testClass = new TestClass( testClass );
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
        return underscore( this.testClass.getJavaClass().getSimpleName() );
    }

    String underscore(String text) {
        text = this.testClass.getJavaClass().getSimpleName();
        text = text.replaceAll( "([A-Z])", "_$1" );
        text = text.toLowerCase();
        text = text.replaceAll( "^_+", "" );
        text = text + ".js";
        return text;
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

                List<FrameworkField> exposedFields = this.testClass.getAnnotatedFields( Expose.class );
                for (FrameworkField each : exposedFields) {
                    String name = each.getField().getName();
                    Object value = each.getField().get( testObj );
                    body.put( name, body.getParentScope(), value );
                }

                List<FrameworkMethod> exposedMethods = this.testClass.getAnnotatedMethods( Expose.class );
                for (FrameworkMethod each : exposedMethods) {
                    String name = each.getName();
                    if (name.startsWith( "get" )) {
                        name = name.substring( 3 );
                        name = name.substring( 0, 1 ).toLowerCase() + name.substring( 1 );

                        Object value = each.invokeExplosively( testObj, new Object[] {} );
                        body.put( name, body.getParentScope(), value );
                    }
                }

                body.call( context, body, body, new Object[] {} );
                
                List<FrameworkMethod> afters = this.testClass.getAnnotatedMethods( After.class );
                for (FrameworkMethod each : afters) {
                    each.invokeExplosively( testObj, new Object[] {} );
                }
                
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
