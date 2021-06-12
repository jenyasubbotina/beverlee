package uz.alex.its.beverlee.view.interfaces;

import uz.alex.its.beverlee.model.transaction.WithdrawalTypeModel.WithdrawalType;

public interface WithdrawalTypeCallback {
    void onWithdrawalTypeSelected(final WithdrawalType withdrawalType);
}
