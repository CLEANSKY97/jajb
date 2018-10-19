package cart;

import java.math.BigDecimal;
import java.util.List;

import com.d_project.jajb.JSONField;
import com.d_project.jajb.JSONType;

@JSONType
public class CartVO {

  @JSONField
  private long cartId;
  @JSONField
  private String comment;
  @JSONField
  private int itemSeqNo;
  @JSONField
  private List<ItemVO> items;

  public long getCartId() {
    return cartId;
  }
  public void setCartId(long cartId) {
    this.cartId = cartId;
  }
  public String getComment() {
    return comment;
  }
  public void setComment(String comment) {
    this.comment = comment;
  }
  public int getItemSeqNo() {
    return itemSeqNo;
  }
  public void setItemSeqNo(int itemSeqNo) {
    this.itemSeqNo = itemSeqNo;
  }
  public List<ItemVO> getItems() {
    return items;
  }
  public void setItems(List<ItemVO> items) {
    this.items = items;
  }

  @JSONType
  public static class ItemVO {

    @JSONField
    private boolean checked;
    @JSONField
    private int itemNo;
    @JSONField
    private String itemName;
    @JSONField
    private String shipDate;
    @JSONField
    private BigDecimal amount;
    @JSONField
    private BigDecimal tax;

    public boolean isChecked() {
      return checked;
    }
    public void setChecked(boolean checked) {
      this.checked = checked;
    }
    public int getItemNo() {
      return itemNo;
    }
    public void setItemNo(int itemNo) {
      this.itemNo = itemNo;
    }
    public String getItemName() {
      return itemName;
    }
    public void setItemName(String itemName) {
      this.itemName = itemName;
    }
    public String getShipDate() {
      return shipDate;
    }
    public void setShipDate(String shipDate) {
      this.shipDate = shipDate;
    }
    public BigDecimal getAmount() {
      return amount;
    }
    public void setAmount(BigDecimal amount) {
      this.amount = amount;
    }
    public BigDecimal getTax() {
      return tax;
    }
    public void setTax(BigDecimal tax) {
      this.tax = tax;
    }

  }
}
