import java.io.InputStream;
import java.io.IOException;

/*
1. exp -> term exp2
2. exp2 -> ^ term exp2
3.        | ε
4. term -> num term2
5. term2 -> & num term2
6.        | ε
7. num -> digit
8.        | (exp)
9. digit -> 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
*/

public class Calc{
    private final InputStream input;
    private int lookahead;

    public Calc(InputStream input) throws IOException{
        this.input = input;
        this.lookahead = input.read();
    }

    private void consume(int symbol) throws IOException, ParseError{
        if(lookahead == symbol)
            lookahead = input.read();
        else
            throw new ParseError();
    }

    private static boolean isDigit(int c){
        return c >= '0' && c <= '9';
    }

    private static int evalDigit(int c){
        return c - '0';
    }

    private int eval() throws IOException, ParseError{
        int goal = exp();
        if(lookahead != -1 && lookahead != '\n')
            throw new ParseError();
        return goal;
    }

    private int exp() throws IOException, ParseError{
        if(lookahead == '(' || isDigit(lookahead)){
            return exp2(term());
        }else{
            throw new ParseError();
        }
    }

    private int exp2(int term) throws IOException, ParseError{
        if(lookahead == '^'){
            consume(lookahead);
            return exp2(term ^ term());
        }else if(lookahead == ')' || lookahead == '\n'){
            return term;
        }else{
            throw new ParseError();
        }
    }

    private int term() throws IOException, ParseError{
        if(lookahead == '(' || isDigit(lookahead)){
            return term2(num());
        }else{
            throw new ParseError();
        }
    }

    private int term2(int num) throws IOException, ParseError{
        if(lookahead == '^' || lookahead == ')' || lookahead == '\n'){
            return num;
        }else if(lookahead == '&'){
            consume(lookahead);
            return term2(num & num());
        }else{
            throw new ParseError();
        }
    }

    private int num() throws IOException, ParseError{
        if(isDigit(lookahead)){
            return digit();
        }else if(lookahead == '('){
            consume(lookahead);
            int InsideParentheses = exp();
            consume(')');
            return InsideParentheses; 
        }else{
            throw new ParseError();
        }
    }

    private int digit() throws IOException, ParseError{
        if(isDigit(lookahead)){
            int next_char = lookahead;
            consume(lookahead);
            return evalDigit(next_char);
        }else{
            throw new ParseError();
        }
    }

    public static void main(String[] args){
        System.out.println("Usage: Input expressions to calculate with numbers, '&', '^' and parentheses");
        try {
            System.out.println((new Calc(System.in)).eval());
        } catch (IOException | ParseError e) {
            System.err.println(e.getMessage());
        }
    }
}