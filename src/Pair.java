public class Pair<F,S>{
    public Pair(F oldField, S newField) {
        this.first = oldField;
        this.second = newField;
    }
    F first;
    S second;
}