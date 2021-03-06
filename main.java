import java.util.Calendar;
import java.util.Scanner;

public class main {
    public static final String ANSI_RESET = "\033[0m";
    public static final String ANSI_BRIGHT_RED    = "\u001B[91m";
    public static final String ANSI_BRIGHT_GREEN  = "\u001B[92m";
    public static final String ANSI_BRIGHT_YELLOW = "\u001B[93m";
    public static final String ANSI_BRIGHT_BLUE   = "\u001B[94m";
    public static final String ANSI_BRIGHT_PURPLE = "\u001B[95m";
    public static final String ANSI_BRIGHT_CYAN   = "\u001B[96m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    public static boolean nieJeCislo(String input) {
        if (input == null) {
            return true;
        }
        try {
            int d = Integer.parseInt(input);
        } catch (NumberFormatException nfe) {
            return true;
        }
        return false;
    }
    public static String Time(){
        String time = "";
        Calendar timeNow = Calendar.getInstance();
        time += timeNow.get(Calendar.HOUR_OF_DAY)+":";
        time += timeNow.get(Calendar.MINUTE)+":";
        time += timeNow.get(Calendar.SECOND);
        return time;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int [][] finalAdd = new int[6][];

        String timeStart = "";
        String timeEnd = "";

        String input = "";
        String [] address = new String[4];



        while(true){
            for (int i = 0; i < 4; i++) {
                int num;
                if (i == 0){
                    num = (int)(Math.random()*223)+0;
                }
                else num = (int)(Math.random()*256)+0;
                address[i] = String.valueOf(num);
            }
            int num = (int)(Math.random()*11)+20;
            input = String.valueOf(num);
            while (true){
                System.out.print("Do you want to start?  (Enter something) : ");
                String a = sc.nextLine();
                System.out.println("\nIP : "+address[0]+"."+address[1]+"."+address[2]+"."+address[3]);
                System.out.println("Prefix : /"+input+"\n");
                timeStart = Time();
                break;
            }

            while (true){
                System.out.print("When you are done, enter something : ");
                String a = sc.nextLine();
                timeEnd = Time();
                break;
            }

            int subnet = Integer.parseInt(input);
            //Finding right spot
            int spot = 0;
            if (subnet > 0 && subnet <= 7) spot = 0;
            if (subnet >= 8 && subnet <= 15) spot = 1;
            if (subnet >= 16 && subnet <= 23) spot = 2;
            if (subnet >= 24 && subnet < 32) spot = 3;


            //getting binary number
            int ad = Integer.parseInt(address[spot]);
            subnet = (subnet%8)+1;
            String bin = "";
            int a = 256;
            for (int i = 0; i < 8; i++) {
                if (ad-(a/2) >= 0){
                    a = a / 2;
                    ad = ad - a;
                    bin +="1";
                }
                else {
                    bin += "0";
                    a = a/2;
                }
            }
            //Getting network
            a = 256;
            int first = 0;
            for (int i = 0; i < subnet-1; i++) {
                a = a/2;
                if (bin.charAt(i) == '1'){
                    first += a;
                }
            }

            //first row will be network
            finalAdd[0] = new int[4];
            for (int i = 0; i < 4; i++) {
                if (i < spot) {
                    finalAdd[0][i] = Integer.parseInt(address[i]);
                }
                if (i == spot) {
                    finalAdd[0][i] = first;
                }
                if (i > spot) {
                    finalAdd[0][i] = 0;
                }
            }

            //Getting broadcast
            bin = bin.substring(subnet);
            int last = 0;
            a = 1;
            for (int i = 0; i < 8-subnet; i++) {
                last += a*2;
                a = a*2;
            }

            //Second row will be broadcast
            finalAdd[1] = new int[4];
            for (int i = 0; i < 4; i++) {
                if (i < spot) {
                    finalAdd[1][i] = Integer.parseInt(address[i]);
                }
                if (i == spot) {
                    finalAdd[1][i] = (first+last+1);
                }
                if (i > spot) {
                    finalAdd[1][i] = 255;
                }
            }

            //Third row will be first host
            finalAdd[2] = new int[4];
            for (int i = 0; i < 4; i++) {
                if (i < 3){
                    finalAdd[2][i] = finalAdd[0][i];
                }
                else {
                    finalAdd[2][i] = (finalAdd[0][i]+1);
                }
            }

            //fourth row will be last host
            finalAdd[3] = new int[4];
            for (int i = 0; i < 4; i++) {
                if (i < 3){
                    finalAdd[3][i] = finalAdd[1][i];
                }
                else {
                    finalAdd[3][i] = (finalAdd[1][i]-1);
                }
            }

            //last row will be hosts
            finalAdd[4] = new int[1];
            int [] s = {1, 3, 7, 15, 31, 63, 127, 255};
            if (spot <3){
                int c = 256,sub = 0;
                if (spot == 0) c = 16777216;
                if (spot == 1) c = 65536;
                sub = c * s[bin.length()];
                if (spot == 2) sub +=254;
                if (spot == 1) sub +=65534;
                if (spot == 0) sub += 16777214;
                finalAdd[4][0] = sub;
            }
            else finalAdd[4][0] = last;

            //Fifth row will be subnet mask
            int [] subn= {255,254,252,248,240,224,192,128,0};
            finalAdd[5] = new int[4];
            for (int i = 0; i < 4; i++) {
                if (i < spot){
                    finalAdd[5][i] = 255;
                }
                if (i == spot){
                    finalAdd[5][i] = subn[bin.length()+1];
                }
                if (i > spot){
                    finalAdd[5][i] = 0;
                }
            }

            //printing results
            System.out.print(ANSI_BRIGHT_GREEN+"\nNetwork\t:\t");
            for (int j = 0; j < finalAdd[0].length; j++) {
                if (j == 3){
                    System.out.print(finalAdd[0][j]);
                }
                else {
                    System.out.print(finalAdd[0][j]+".");
                }
            }
            System.out.print(ANSI_BRIGHT_CYAN+"\nBroadcast\t:\t");
            for (int j = 0; j < finalAdd[1].length; j++) {
                if (j == 3){
                    System.out.print(finalAdd[1][j]+ANSI_RESET);
                }
                else {
                    System.out.print(finalAdd[1][j]+".");
                }
            }
            System.out.print(ANSI_BRIGHT_BLUE+"\nSubnet Mask\t:\t");
            for (int i = 0; i < 4; i++) {
                if (i == 3){
                    System.out.print(finalAdd[5][i]+ANSI_RESET);
                }
                else {
                    System.out.print(finalAdd[5][i]+".");
                }
            }
            System.out.print(ANSI_BRIGHT_YELLOW+"\nFirst / "+ANSI_RESET+ANSI_BRIGHT_PURPLE+"Last Host"+ANSI_RESET+ANSI_BRIGHT_YELLOW+"\t:\t");
            for (int j = 0; j < finalAdd[2].length; j++) {
                if (j == 3){
                    System.out.print(finalAdd[2][j]+ANSI_RESET);
                }
                else {
                    System.out.print(finalAdd[2][j]+".");
                }
            }
            System.out.print(ANSI_RESET+" - "+ANSI_BRIGHT_PURPLE);
            for (int j = 0; j < finalAdd[3].length; j++) {
                if (j == 3){
                    System.out.print(finalAdd[3][j]+ANSI_RESET);
                }
                else {
                    System.out.print(finalAdd[3][j]+".");
                }
            }
            System.out.println(ANSI_BRIGHT_RED+"\nHosts\t:\t"+finalAdd[4][0]+ANSI_RESET);

            String IPclass = "";
            int ip = Integer.parseInt(address[0]);
            if (ip >= 1 && ip <= 127) IPclass = "A";
            if (ip >= 128 && ip <= 191) IPclass = "B";
            if (ip >= 192 && ip <= 223) IPclass = "C";
            System.out.println(ANSI_PURPLE+"IP Class : "+IPclass+ANSI_RESET);

            System.out.println("\n\nTime of start :  "+timeStart+"\nFinished on :  "+timeEnd);

            while (true){
                System.out.println("\n\nDo you want to continue (If not enter 'n' , if yes enter anything): ");
                String p = sc.nextLine().replaceAll(" +","");
                if (p.equals("n")){
                    System.out.println(ANSI_BRIGHT_CYAN+"\nThanks for using !"+ANSI_RESET);
                    System.exit(0);
                }
                else {
                    System.out.println("\n\n\n\n\n\n\n\n");
                    break;
                }
            }
        }
    }
}
