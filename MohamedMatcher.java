import java.util.Random;

public class MohamedMatcher {
    public String Match(String Pattern , String TextToMatch, boolean WithBeforeAndAfter){
        String ReturnVal = "";
        try{
            if (TextToMatch.contains("<match>")){
                String Before, After;
                Before = TextToMatch.split("<match>")[0];
                if (TextToMatch.endsWith("<match>")){

                    if (Pattern.contains(Before)){

                        if (WithBeforeAndAfter){
                            ReturnVal = Before + Pattern.split(Before)[1];
                        }
                        else{
                            ReturnVal = Pattern.split(Before)[1];
                        }
                    }
                }
                else{
                    After = TextToMatch.split("<match>")[1];

                    if (Pattern.contains(Before) && Pattern.contains(After)){
                        if(WithBeforeAndAfter){
                            ReturnVal = Before + Pattern.split(Before)[1].split(After)[0] + After;
                        }
                        else{
                            ReturnVal = Pattern.split(Before)[1].split(After)[0];
                        }

                    }
                }

            }
        }
        catch (Exception e){

        }

        return ReturnVal;
    }

    public int countOf(String Pattern , String val){
        int ReturnVal = 0;
        try
        {
            char[] PatternChars = Pattern.toCharArray();
            String str = "";
            for (char chr : PatternChars){
                str += String.valueOf(chr);
                if (str.contains(val)){
                    ReturnVal += 1;
                    str = "";
                }
            }
        }catch (Exception e){

        }


        return ReturnVal;
    }
    private Random random = new Random();
    String GenerateRandom(int max){
        var val = "";
        var letters = "1234567890qwertyuioplkjhgfdsazxcvbnm";
        for (int i = 0; i < max; i+=1){
            val += String.valueOf(letters.charAt(random.nextInt(letters.length())));
        }
        return val;
    }
    String GenerateRandom(int max, String letters){
        var val = "";
        for (int i = 0; i < max; i+=1){
            val += String.valueOf(letters.charAt(random.nextInt(letters.length())));
        }
        return val;
    }

    String Unescape(String input) {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (i < input.length()) {
            char delimiter = input.charAt(i); i++; 

            if(delimiter == '\\' && i < input.length()) {

                
                char ch = input.charAt(i); i++;

                if(ch == '\\' || ch == '/' || ch == '"' || ch == '\'') {
                    builder.append(ch);
                }
                else if(ch == 'n') builder.append('\n');
                else if(ch == 'r') builder.append('\r');
                else if(ch == 't') builder.append('\t');
                else if(ch == 'b') builder.append('\b');
                else if(ch == 'f') builder.append('\f');
                else if(ch == 'u') {

                    StringBuilder hex = new StringBuilder();

                    // expect 4 digits
                    if (i+4 > input.length()) {
                       return "";
                    }
                    for (char x : input.substring(i, i + 4).toCharArray()) {
                        if(!Character.isLetterOrDigit(x)) {
                            return "";
                        }
                        hex.append(Character.toLowerCase(x));
                    }
                    i+=4; // consume those four digits.

                    int code = Integer.parseInt(hex.toString(), 16);
                    builder.append((char) code);
                }
            } else {
                builder.append(delimiter);
            }
        }
        return builder.toString();
    }
}
