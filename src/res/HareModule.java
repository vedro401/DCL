package res;
public class HareModule {
    String size = "XL";
    String color = "White";
    Duck filling = new Duck();
    public class Duck{
        int weight = 666;
        boolean quacks = true;
        Egg filling = new Egg();
        class Egg{
            boolean isSteel = false;
            Needle filling = new Needle();
            private class Needle{
                String containsTheDeathOfTheKoshchei = "Nop";
                float diameter = 0.3f;
            }
        }
    }

}
