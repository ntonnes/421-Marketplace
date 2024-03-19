public class HomePage implements Page {

    @Override
    public void display() {

        App.push(this);
        System.out.println("Menu:\n\t(1) Search\n\t(2) View Cart\n\t(3) View Past Orders\n\t(4) Leave a Review \n\t(5) ");
    }  
}
