package com.revolt.service;

import com.revolt.model.*;
import com.revolt.repository.PointsLedgerRepository;
import com.revolt.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PointsWalletService {

    private final UserRepository userRepository;
    private final PointsLedgerRepository ledgerRepository;

    public PointsWalletService(UserRepository userRepository, PointsLedgerRepository ledgerRepository) {
        this.userRepository = userRepository;
        this.ledgerRepository = ledgerRepository;
    }

    @Transactional
    public void creditInitialTradeInPoints(User user, Long tradeInDeviceId, Long points) {
        user.setAvailablePoints(user.getAvailablePoints() + points);
        userRepository.save(user);

        PointsLedger ledger = new PointsLedger(
                user.getId(), null, tradeInDeviceId, points,
                PointsTransactionType.EARNED, user.getAvailablePoints(),
                "Trade-in valuation initial credit (7-day Price Lock active)"
        );
        ledgerRepository.save(ledger);
    }

    @Transactional
    public void holdPointsForOrder(User user, Order order, Long pointsToHold) {
        if (user.getAvailablePoints() < pointsToHold) {
            throw new IllegalArgumentException("Insufficient available points. Requested: " + pointsToHold + 
                    ", Available: " + user.getAvailablePoints());
        }

        user.setAvailablePoints(user.getAvailablePoints() - pointsToHold);
        user.setHeldPoints(user.getHeldPoints() + pointsToHold);
        userRepository.save(user);

        PointsLedger ledger = new PointsLedger(
                user.getId(), order.getId(), order.getTradeInDeviceId(), pointsToHold,
                PointsTransactionType.ON_HOLD, user.getAvailablePoints(),
                "Points put ON_HOLD for Order #" + order.getOrderNumber() + " pending doorstep inspection"
        );
        ledgerRepository.save(ledger);
    }

    @Transactional
    public void finalizeHeldPoints(User user, Order order) {
        Long pointsHeld = order.getPointsRedeemed();
        if (user.getHeldPoints() < pointsHeld) {
            user.setHeldPoints(0L);
        } else {
            user.setHeldPoints(user.getHeldPoints() - pointsHeld);
        }
        userRepository.save(user);

        PointsLedger ledger = new PointsLedger(
                user.getId(), order.getId(), order.getTradeInDeviceId(), pointsHeld,
                PointsTransactionType.REDEEMED, user.getAvailablePoints(),
                "Points finalized and redeemed for Order #" + order.getOrderNumber() + " after verified doorstep trade-in"
        );
        ledgerRepository.save(ledger);
    }

    @Transactional
    public void releaseHeldPointsOnCancel(User user, Order order) {
        Long pointsHeld = order.getPointsRedeemed();
        if (user.getHeldPoints() >= pointsHeld) {
            user.setHeldPoints(user.getHeldPoints() - pointsHeld);
        } else {
            user.setHeldPoints(0L);
        }
        user.setAvailablePoints(user.getAvailablePoints() + pointsHeld);
        userRepository.save(user);

        PointsLedger ledger = new PointsLedger(
                user.getId(), order.getId(), order.getTradeInDeviceId(), pointsHeld,
                PointsTransactionType.CANCELLED, user.getAvailablePoints(),
                "Held points released back to wallet due to order/inspection cancellation for #" + order.getOrderNumber()
        );
        ledgerRepository.save(ledger);
    }

    @Transactional
    public void cancelHeldPointsOnFraudOrMismatch(User user, Order order, String reason) {
        Long pointsHeld = order.getPointsRedeemed();
        if (user.getHeldPoints() >= pointsHeld) {
            user.setHeldPoints(user.getHeldPoints() - pointsHeld);
        } else {
            user.setHeldPoints(0L);
        }
        userRepository.save(user);

        PointsLedger ledger = new PointsLedger(
                user.getId(), order.getId(), order.getTradeInDeviceId(), pointsHeld,
                PointsTransactionType.CANCELLED, user.getAvailablePoints(),
                "Held points cancelled due to inspection mismatch/fraud: " + reason
        );
        ledgerRepository.save(ledger);
    }
}
