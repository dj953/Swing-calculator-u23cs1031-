package calculator.logic;

public class CalculatorLogic {
    private double result = 0;
    private String lastCommand = "=";
    
    public double calculate(double x, String command) {
        switch (lastCommand) {
            case "+": result += x; break;
            case "-": result -= x; break;
            case "*": result *= x; break;
            case "/": 
                if (x == 0) throw new ArithmeticException("Division by zero");
                result /= x; 
                break;
            case "%": 
                if (x == 0) throw new ArithmeticException("Division by zero");
                result %= x; 
                break;
            case "=": result = x; break;
        }
        
        lastCommand = command;
        return result;
    }
    
    public void clear() {
        result = 0;
        lastCommand = "=";
    }
}