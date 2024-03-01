/* -*- java -*-
# =========================================================================== #
#                                                                             #
#                         Copyright (C) KNAPP AG                              #
#                                                                             #
#       The copyright to the computer program(s) herein is the property       #
#       of Knapp.  The program(s) may be used   and/or copied only with       #
#       the  written permission of  Knapp  or in  accordance  with  the       #
#       terms and conditions stipulated in the agreement/contract under       #
#       which the program(s) have been supplied.                              #
#                                                                             #
# =========================================================================== #
*/

package com.knapp.codingcontest.solution;

import com.knapp.codingcontest.data.InputData;
import com.knapp.codingcontest.data.Institute;
import com.knapp.codingcontest.data.Order;
import com.knapp.codingcontest.data.Product;
import com.knapp.codingcontest.operations.CostFactors;
import com.knapp.codingcontest.operations.InfoSnapshot;
import com.knapp.codingcontest.operations.Warehouse;
import com.knapp.codingcontest.operations.WorkStation;
import com.knapp.codingcontest.operations.ex.*;

import java.util.*;

/**
 * This is the code YOU have to provide
 */
public class Solution {
    protected final Warehouse warehouse;
    protected final WorkStation workStation;

    // ----------------------------------------------------------------------------
    protected final InputData input;

    public Solution(final Warehouse warehouse, final InputData input) {
        this.warehouse = warehouse;
        workStation = warehouse.getWorkStation();
        this.input = input;

        // TODO: prepare data structures (but may also be done in run() method below)
    }

    public String getParticipantName() {
        /* lines containing '@TODO' are removed before packaging contest-sandbox*/
        return "Linus Freistetter"; // TODO: return your name
    }

    // ----------------------------------------------------------------------------

    public Institute getParticipantInstitution() {
        /* lines containing '@TODO' are removed before packaging contest-sandbox*/
        return Institute.HTL_Rennweg_Wien; // TODO: return the Id of your institute - please refer to the hand-out
    }

    // ----------------------------------------------------------------------------

    /**
     * The main entry-point.
     */
    public void run() throws Exception {
        Collection<Order> orders = input.getAllOrders();
        ArrayList<Product> inWs = new ArrayList<>();
        orders.iterator().forEachRemaining(o -> {
            try {
                Collections.sort(inWs);
                workStation.startOrder(o);
                List<Product> inOrder = o.getOpenProducts();
                while (!inOrder.isEmpty()) {
                    Product product = inOrder.getFirst();
                    if (!workStation.getAssignedProducts().contains(product) && inWs.size() < 4) {
                        workStation.assignProduct(product);
                        inWs.add(product);
                    } else if (inWs.size() == 4 && !workStation.getAssignedProducts().contains(product)) {
                        workStation.removeProduct(inWs.getFirst().equals(product) ? inWs.get(1) : inWs.getFirst());
                        inWs.remove(inWs.getFirst().equals(product) ? inWs.get(1) : inWs.getFirst());
                        workStation.assignProduct(product);
                        inWs.add(product);
                    }
                    workStation.pickOrder(o, product);

                    inOrder = o.getOpenProducts();

                }
            } catch (NoSuchProductException | ProductNotAssignedException | OrderAlreadyStartedException |
                     NoEmptyOrderSlotAvailableException | DuplicateProductException |
                     NoEmptyProductSlotAvailableException | OrderNotActiveException | NoSuchProductInOrderException |
                     NoSuchProductInOrderLeftException e) {
                throw new RuntimeException(e.getClass().getSimpleName() + ": " + workStation.getActiveOrders() + ",prd: " + workStation.getAssignedProducts());
            }
        });


        // TODO: make calls to API (see below)
        // lines containing '@TODO' are removed before packaging contest-sandbox
    }


// ----------------------------------------------------------------------------
// ----------------------------------------------------------------------------

    /**
     * Just for documentation purposes.
     * <p>
     * Method may be removed without any side-effects
     * divided into these sections
     *
     * <li><em>input methods</em>
     *
     * <li><em>main interaction methods</em>
     * - these methods are the ones that make (explicit) changes to the warehouse
     *
     * <li><em>information</em>
     * - information you might need for your solution
     *
     * <li><em>additional information</em>
     * - various other infos: statistics, information about (current) costs, ...
     */
    @SuppressWarnings("unused")
    private void apis() throws Exception {
        // ----- input -----

        final Collection<Order> orders = input.getAllOrders();

        final Order order = orders.iterator().next();
        final Product product = order.getOpenProducts().getFirst();

        final WorkStation workStation = warehouse.getWorkStation();

        // ----- main interaction methods -----

        workStation.startOrder(order);
        workStation.assignProduct(product);
        workStation.removeProduct(product);
        workStation.pickOrder(order, product);

        // ----- information -----

        final List<Product> aps = order.getAllProducts();
        final List<Product> ops = order.getOpenProducts();
        final boolean ofin = order.isFinished();

        final int wsos = workStation.getOrderSlots();
        final int wsps = workStation.getProductSlots();

        final Set<Order> waaos = workStation.getActiveOrders();
        final Set<Product> wsaps = workStation.getAssignedProducts();

        // ----- additional information -----

        final CostFactors costFactors = input.getCostFactors();
        final double cf_pa = costFactors.getProductAssignmentCost();
        final double cf_up = costFactors.getUnfinishedProductPenalty();

        final InfoSnapshot info = warehouse.getInfoSnapshot();

        final int up = info.getUnfinishedProductCount();
        final int oso = info.getOperationCount(InfoSnapshot.OperationType.StartOrder);
        final int oap = info.getOperationCount(InfoSnapshot.OperationType.AssignProduct);
        final int orp = info.getOperationCount(InfoSnapshot.OperationType.RemoveProduct);
        final int opo = info.getOperationCount(InfoSnapshot.OperationType.PickOrder);

        final double c_uo = info.getUnfinishedOrdersCost();
        final double c_pa = info.getProductAssignmentCost();
        final double c_t = info.getTotalCost();
    }
}
// ----------------------------------------------------------------------------

