package cart;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.d_project.jajb.rpc.Callable;

import cart.CartVO.ItemVO;

public class CartService {

  @Callable
  public CartVO newCart() {
    CartVO cartVO = new CartVO();
    cartVO.setCartId(System.nanoTime() );
    cartVO.setComment("");
    cartVO.setItems(new ArrayList<ItemVO>() );
    return cartVO;
  }

  @Callable
  public CartVO addItem(CartVO cartVO) {
    ItemVO itemVO = new ItemVO();
    itemVO.setItemName("Name of Item"); 
    itemVO.setAmount(BigDecimal.valueOf(100) ); 
    itemVO.setTax(BigDecimal.valueOf(10) ); 
    itemVO.setShipDate(new SimpleDateFormat().format(new Date() ) ); 
    cartVO.getItems().add(itemVO);
    return cartVO;
  }

  @Callable
  public CartVO removeItems(CartVO cartVO) {
    List<ItemVO> newItems = new ArrayList<ItemVO>();
    for (ItemVO itemVO : cartVO.getItems() ) {
      if (!itemVO.isChecked() ) {
        newItems.add(itemVO);
      } else {
        // checked for removal.
      }
    }
    cartVO.setItems(newItems);
    return cartVO;
  }

}
