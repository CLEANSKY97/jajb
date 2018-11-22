package cart;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.d_project.jajb.rpc.Callable;

import cart.CartVO.ItemVO;

@Service("CartService")
public class CartService {

  @Autowired
  private UserMapper userMapper;

  @Callable
  public CartVO newCart() {

    System.out.println("user:" + userMapper.getUser("test") );

    // create a new cart with no items.
    CartVO cartVO = new CartVO();
    cartVO.setCartId(System.nanoTime() );
    cartVO.setComment("");
    cartVO.setItemSeqNo(1);
    cartVO.setItems(new ArrayList<ItemVO>() );
    return cartVO;
  }

  @Callable
  public CartVO addItem(CartVO cartVO) {

    // create a new item.
    ItemVO itemVO = new ItemVO();
    itemVO.setItemNo(cartVO.getItemSeqNo() );
    itemVO.setItemName("Name of Item"); 
    itemVO.setAmount(BigDecimal.valueOf(100) ); 
    itemVO.setTax(BigDecimal.valueOf(10) ); 
    itemVO.setShipDate(new SimpleDateFormat().format(new Date() ) ); 

    cartVO.getItems().add(itemVO);

    // increment a seq-no.
    cartVO.setItemSeqNo(cartVO.getItemSeqNo() + 1);

    return cartVO;
  }

  @Callable
  public CartVO removeItems(CartVO cartVO) {
    int removeCount = 0;
    List<ItemVO> newItems = new ArrayList<ItemVO>();
    for (ItemVO itemVO : cartVO.getItems() ) {
      if (!itemVO.isChecked() ) {
        newItems.add(itemVO);
      } else {
        // checked for removal.
        removeCount += 1;
      }
    }
    if (removeCount == 0) {
      throw new ApplicationException("nothing to remove.");
    }
    cartVO.setItems(newItems);
    return cartVO;
  }

}
