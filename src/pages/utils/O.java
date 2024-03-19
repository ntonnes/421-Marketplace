package pages.utils;

public class O {
    public static void print(String s){
        System.out.println(s);
    }

    public static void print(int i){
        System.out.println(i);
    }

    public static void print(double d){
        System.out.println(d);
    }

    public static void print(boolean b){
        System.out.println(b);
    }

    public static void print(Object o){
        System.out.println(o);
    }

    public static void print(){
        System.out.println();
    }



    public static void menuCMD (){
        O.print();
        O.print("-----------------Welcome to the 421 Marketplace!---------------------------------");
        O.print();
        O.print("| 1.Back |    | 2.Home |               | 3. Cart |     | 3.Login |    | 4.Signup |");
        O.print("----------------------------------------------------------------------------------");
        O.print();
        O.print("                       5. Search");
        O.print("                      6. My Account");
        O.print("                      7. View Orders");
    }
}
