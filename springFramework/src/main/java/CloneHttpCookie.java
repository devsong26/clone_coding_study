import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class CloneHttpCookie {

    private final String name;
    private final String value;

    public CloneHttpCookie(String name, @Nullable String value){
        Assert.hasLength(name, "'name' is required and must not be empty.");
        this.name = name;
        this.value = (value != null ? value : "");
    }

    public String getName(){
        return this.name;
    }

    public String getValue(){
        return this.value;
    }

    @Override
    public int hashCode(){
        return this.name.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object other){
        if (this == other){
            return true;
        }
        if (!(other instanceof CloneHttpCookie)){
            return false;
        }
        CloneHttpCookie otherCookie = (CloneHttpCookie) other;
        return (this.name.equalsIgnoreCase(otherCookie.getName()));
    }

    @Override
    public String toString(){
        return this.name + "=" + this.value;
    }

}
