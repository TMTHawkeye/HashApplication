package co.lujun.androidtagview;

public class Tagmodel {
    String mname;
    Boolean mbol;
  public   Tagmodel(String name ,boolean bol){
        this.mname=name;
        this.mbol=bol;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public Boolean getMbol() {
        return mbol;
    }

    public void setMbol(Boolean mbol) {
        this.mbol = mbol;
    }
}
