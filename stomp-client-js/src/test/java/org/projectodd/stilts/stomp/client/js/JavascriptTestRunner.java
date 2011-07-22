package org.projectodd.stilts.stomp.client.js;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.ScriptableObject;

public class JavascriptTestRunner extends Runner {

    private Class<? extends JavascriptTestCase> testClass;
    private Context context;
    private ScriptableObject scope;
    private ScriptableObject window;
    private Object server;
    private Description description;

    public JavascriptTestRunner(Class<? extends JavascriptTestCase> testClass) throws Exception {
        this.testClass = testClass;
        System.err.println( "simpleName = " + testClass.getSimpleName() );
        setUpRhino();
        loadTests();
    }

    void setUpRhino() throws Exception {
        this.context = Context.enter();
        this.scope = this.context.initStandardObjects();
        // prepare websocket support;
        this.window = (ScriptableObject) this.context.evaluateString( this.scope, "var window = {}; window;", "<cmd>", 1, null );
        this.window.put( "server", this.window, this.server );
        evaluateResource( "jspec.js" );
    }

    public Object evaluateResource(String name) throws IOException {
        InputStream in = getClass().getResourceAsStream( name );
        Reader reader = new InputStreamReader( in );
        try {
            return this.context.evaluateReader( this.window, reader, name, 1, null );
        } finally {
            reader.close();
        }
    }

    void loadTests() throws Exception {
        evaluateResource( this.testClass.getSimpleName() + ".js" );
        setUpDescription();
    }

    @Override
    public Description getDescription() {
        return this.description;
    }

    void setUpDescription() {
        this.description = Description.createSuiteDescription( this.testClass.getSimpleName() );
        NativeArray result = (NativeArray) this.window.get( "tests", this.window );
        long len = result.getLength();

        for (int i = 0; i < len; ++i) {
            ScriptableObject test = (ScriptableObject) result.get( i, result );
            String testName = (String) test.get( "description", test );
            System.err.println( "testName=" + testName );
            Description child = Description.createTestDescription( this.testClass, testName );
            description.addChild( child );
        }
    }

    @Override
    public void run(RunNotifier notifier) {
        System.err.println( notifier );
        int i = 0;
        NativeArray tests = (NativeArray) this.window.get( "tests", this.window );

        for (Description testDescription : this.description.getChildren()) {
            System.err.println( "START: " + testDescription.getMethodName() );
            ScriptableObject test = (ScriptableObject) tests.get( i, tests );
            Function body = (Function) test.get( "body", test );
            Context.enter();
            try {
                notifier.fireTestStarted( testDescription );
                body.call( context, body, body, new Object[] {} );
            } catch (AssertionError e) {
                notifier.fireTestFailure( new Failure( testDescription, e ) );
            } finally {
                Context.exit();
                notifier.fireTestFinished( testDescription );
            }
            ++i;
        }
    }

}
