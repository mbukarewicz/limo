package structures;

import com.mutunus.tutunus.structures.*;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class TradingRegisterImplTest {

    private static final BigDecimal BROKERAGE = new BigDecimal("3");

    private static final MTDate D1 = new MTDate(2011, 5, 15);
    private static final MTDate D2 = new MTDate(2011, 5, 20);
    private static final MTDate D3 = new MTDate(2011, 5, 21);

    private static final Transaction T100B = new Transaction(1, Side.LONG, 100, new BigDecimal(10), BROKERAGE, D1);
    private static final Transaction T50B = new Transaction(1, Side.LONG, 50, new BigDecimal(12), BROKERAGE, D1);
    private static final Transaction T100S = new Transaction(1, Side.SHORT, 100, new BigDecimal(22), BROKERAGE, D1);
    private static final Transaction T50S = new Transaction(1, Side.SHORT, 50, new BigDecimal(7), BROKERAGE, D1);

    private TradingRegisterImpl flow = new TradingRegisterImpl("asset");

    @Test
    public void basicsTest() {
        runBasicTest(T100B, T50B);
        flow = new TradingRegisterImpl("asset");
        runBasicTest(T100S, T50S);
    }

    @Test
    public void sameTransactionsMixed() {
        flow.addTransaction(t(1, D1, T100B));
        flow.addTransaction(t(1, D1, T50B));
        flow.addTransaction(t(1, D1, T100B));
        flow.addTransaction(t(1, D1, T50B));
        assertEquals(300, flow.getOpenedPositionSize(D1));
        assertEquals(0, flow.getNewOrClosedTradesForDate(D1).size());
    }

    private Transaction t(int tickId, final MTDate date, final Transaction t) {
        return new Transaction(tickId, t.getSide(), t.getSize(), t.getPrice(), t.getBrokerage(), date);
    }

    public void runBasicTest(final Transaction t1, final Transaction t2) {
        int size = 0;

        flow.addTransaction(t(1, D1, t1));
        flow.seal();
        size += t1.getSide().getSizeSigned(t1.getSize());
        assertEquals(size, flow.getOpenedPositionSize(D1));
        assertEquals(0, flow.getNewOrClosedTradesForDate(D1).size());

        flow.addTransaction(t(1, D1, t2));
        flow.seal();
        size += t2.getSide().getSizeSigned(t2.getSize());
        assertEquals(size, flow.getOpenedPositionSize(D1));
        assertEquals(0, flow.getNewOrClosedTradesForDate(D1).size());

        flow.addTransaction(t(1, D2, t1));
        flow.seal();
        size += t1.getSide().getSizeSigned(t1.getSize());
        assertEquals(size, flow.getOpenedPositionSize(D2));
        assertEquals(0, flow.getNewOrClosedTradesForDate(D2).size());

        flow.addTransaction(t(1, D2, t2));
        flow.seal();
        size += t2.getSide().getSizeSigned(t2.getSize());
        assertEquals(size, flow.getOpenedPositionSize(D2));
        assertEquals(0, flow.getNewOrClosedTradesForDate(D2).size());

        assertEquals(size, flow.getOpenedPositionSize(D3));
        assertEquals(0, flow.getNewOrClosedTradesForDate(D3).size());
    }

    // @Test
    // public void samePriceOneEntry() {
    // flow.addTransaction(t(D1, T1B));
    // flow.addTransaction(t(D1, T1B));
    // assertEquals(1, flow.getOpenPositions(D1).size());
    // assertEquals(0, flow.getNewOrClosedTradesForDate(D1).size());
    //
    // final TradableBase position = flow.getOpenPositions(D1).get(0);
    // assertEquals(T1B.getSize() * 2, position.getSize());
    // }

    @Test
    public void buySellTest1() {
        flow.addTransaction(t(1, D1, T100B));
        flow.addTransaction(t(1, D1, T100S));

        assertEquals(0, flow.getOpenedPositionSize(D1));
        assertEquals(0, flow.getNewOrClosedTradesForDate(D1).size());
        // assertEquals(100 * 12., flow.getTotalProfit(D1).doubleValue(), 0.001);
    }

    @Test
    public void buySellTest2() {
        flow.addTransaction(t(1, D1, T100B));// +100
        flow.addTransaction(t(1, D1, T50S));// -50
        flow.addTransaction(t(1, D1, T50S));// -50

        assertEquals(0, flow.getOpenedPositionSize(D1));
        assertEquals(0, flow.getNewOrClosedTradesForDate(D1).size());
        // assertEquals(-2 * 50 * 3., flow.getTotalProfit(D1).doubleValue(), 0.001);
    }

    @Test
    public void brokerageTest() {
        flow.addTransaction(t(1, D1, T50B));// +50
        assertEquals(3, flow.getTotalBrokerage(D1).doubleValue(), 0.001);

        flow.addTransaction(t(1, D1, T50B));// +50
        flow.seal();
        assertEquals(6, flow.getTotalBrokerage(D1).doubleValue(), 0.001);

        flow.addTransaction(t(1, D1, T100S));// -100
        flow.seal();
        assertEquals(9, flow.getTotalBrokerage(D1).doubleValue(), 0.001);
    }

    @Test
    public void brokerageTest2() {
        flow.addTransaction(t(1, D1, T100B));// +100
        flow.addTransaction(t(1, D1, T50S));// -50
        assertEquals(6, flow.getTotalBrokerage(D1).doubleValue(), 0.001);

        flow.addTransaction(t(1, D1, T50S));// -50
        flow.seal();
        assertEquals(9, flow.getTotalBrokerage(D1).doubleValue(), 0.001);
    }

    @Test
    public void tradeTest1() {
        final Trade trade = new Trade(T100B, t(1, D3, T100S));
        flow.addTrade(trade);

        flow.seal();
        assertEquals(100, flow.getOpenedPositionSize(D1));
        assertEquals(100, flow.getOpenedPositionSize(D2));
        assertEquals(0, flow.getOpenedPositionSize(D3));

        assertEquals(-BROKERAGE.doubleValue(), flow.getTotalNetProfit(D1).doubleValue(), 0.001);
        assertEquals(-BROKERAGE.doubleValue(), flow.getTotalNetProfit(D2).doubleValue(), 0.001);
        assertEquals(12 * 100 - 6, flow.getTotalNetProfit(D3).doubleValue(), 0.001);
    }
}
