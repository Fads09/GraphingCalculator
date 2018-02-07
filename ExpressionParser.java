import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ExpressionParser {
		ScriptEngine engine;
		
		ExpressionParser()
		{
			ScriptEngineManager manager = new ScriptEngineManager();
		    engine = manager.getEngineByName("JavaScript");//setup the engine
		}
		//Evaluate the expression
		public double evaluate(String expression) throws ScriptException 
		{
			String result = (String) engine.eval(expression);
			return new Double(result);
		}
	}