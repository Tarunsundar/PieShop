


public class DenominationFloat {
    private UKDenomination name;

    private int quantity;

    public DenominationFloat(UKDenomination n){
        this(n, 0);
    }

    public DenominationFloat(UKDenomination n, int q){
        name = n;
        quantity = q;
    }

    public UKDenomination getType() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setType(UKDenomination denom){
        this.name = denom;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DenominationFloat other = (DenominationFloat) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return quantity + " x " + name;
    }

}