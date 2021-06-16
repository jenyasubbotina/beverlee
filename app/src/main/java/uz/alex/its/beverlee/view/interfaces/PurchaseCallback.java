package uz.alex.its.beverlee.view.interfaces;

import uz.alex.its.beverlee.model.transaction.PurchaseModel;

public interface PurchaseCallback {
    void onDeleteFromBasketClicked(final PurchaseModel.Purchase item, final int position);
    void onPurchaseBtnClicked(final PurchaseModel.Purchase item, final int position);
}
