package units;

public class TestInputProvider implements interfaces.InputProvider {
    private final String input;
    public TestInputProvider(String input) { this.input = input; }
    @Override
    public String getInput() { return input; }
}