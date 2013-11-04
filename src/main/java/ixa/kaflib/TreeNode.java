package ixa.kaflib;
import org.jdom2.Element;

public interface TreeNode {

    public String getId();
    public boolean hasEdgeId();
    public String getEdgeId();
    public void setEdgeId(String edgeId);
    public boolean getHead();

}
