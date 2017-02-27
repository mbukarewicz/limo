package structures;

import com.mutunus.tutunus.structures.*;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class TradingRegisterFactoryTest {

    private static final int BR = 3;
    private static final BigDecimal BROKERAGE = new BigDecimal(BR);

    private static final MTDate D1 = new MTDate(2011, 5, 15);
    private static final MTDate D2 = new MTDate(2011, 5, 20);
    private static final MTDate D3 = new MTDate(2011, 5, 21);

    private static final Transaction T100B10 = new Transaction(1, Side.LONG, 100, new BigDecimal(10), BROKERAGE, D1);
    private static final Transaction T50B12 = new Transaction(1, Side.LONG, 50, new BigDecimal(12), BROKERAGE, D1);
    private static final Transaction T100S22 = new Transaction(1, Side.SHORT, 100, new BigDecimal(22), BROKERAGE, D1);
    private static final Transaction T50S7 = new Transaction(1, Side.SHORT, 50, new BigDecimal(7), BROKERAGE, D1);

    FlowFactory ff = new FlowFactory("asset");

    @Test
    public void test1() {
        ff.addFifoTransaction(t(1, D1, T100B10));
        ff.addFifoTransaction(t(1, D1, T50B12));
        ff.addFifoTransaction(t(1, D1, T100B10));
        ff.addFifoTransaction(t(1, D1, T50B12));

        assertEquals(300, ff.getOpenedPositionSize(D1));

        final TradingRegister tradingRegister = ff.getFlow();
        assertEquals(300, tradingRegister.getOpenedPositionSize(D1));
        assertEquals(0, tradingRegister.getNewOrClosedTradesForDate(D1).size());
    }

    @Test
    public void buySellTest1() {
        ff.addFifoTransaction(t(1, D1, T100B10));
        ff.addFifoTransaction(t(1, D1, T100S22));

        assertEquals(0, ff.getOpenedPositionSize(D1));

        final TradingRegister tradingRegister = ff.getFlow();
        assertEquals(0, tradingRegister.getOpenedPositionSize(D1));
        assertEquals(1, tradingRegister.getNewOrClosedTradesForDate(D1).size());
        assertEquals(100 * 12. - 2 * BR, tradingRegister.getTotalNetProfit(D1).doubleValue(), 0.001);
    }

    @Test
    public void buySellTest2() {
        ff.addFifoTransaction(t(1, D1, T100B10));// +100
        ff.addFifoTransaction(t(1, D1, T50S7));// -50
        ff.addFifoTransaction(t(1, D1, T50S7));// -50

        final TradingRegister tradingRegister = ff.getFlow();
        assertEquals(0, tradingRegister.getOpenedPositionSize(D1));
        assertEquals(2, tradingRegister.getNewOrClosedTradesForDate(D1).size());
        assertEquals(-2 * 50 * 3. - 3 * BR, tradingRegister.getTotalNetProfit(D1).doubleValue(), 0.001);
    }

    private Transaction t(int tickId, final MTDate date, final Transaction t) {
        return new Transaction(tickId, t.getSide(), t.getSize(), t.getPrice(), t.getBrokerage(), date);
    }

}
