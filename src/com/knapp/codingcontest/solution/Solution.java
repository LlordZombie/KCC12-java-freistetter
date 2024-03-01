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
import java.util.concurrent.atomic.AtomicInteger;

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


    }

    public String getParticipantName() {

        return "Linus Freistetter";
    }

    // ----------------------------------------------------------------------------

    public Institute getParticipantInstitution() {

        return Institute.HTL_Rennweg_Wien;
    }

    // ----------------------------------------------------------------------------

    /**
     * The main entry-point.
     */
    public void run() {//TODO: vier Orders laden, in Array speichern und optimalPrds anwenden
        Collection<Order> orders = input.getAllOrders();
        ArrayList<Product> inWs = new ArrayList<>();
       // AtomicInteger c = new AtomicInteger();

        orders.iterator().forEachRemaining(o -> {
            try {/*
                Collections.sort(inWs);
                workStation.startOrder(o);
                if (c.get() %4==3){
                    Product[] p = getOptimalPrds();
                    for (int i = 0; i < 4; i++) {
                        inWs.add(p[i]);
                        workStation.assignProduct(p[i]);
                    }
                    for (int i = 0; i < 4; i++) {
                        List<Product> inOrder = o.getOpenProducts();
                        List<Product> doAble = new ArrayList<>();
                        inOrder.forEach(product -> {
                            if (contains(p,product)){
                                doAble.add(product);
                            }
                        });
                        workStation.getActiveOrders().forEach(ord->{
                            ord.getOpenProducts().forEach(prd->{

                            });
                        });
                    }
                }
                c.getAndIncrement();
                */
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
                    for (int i = 0; i < getNumInOrder(o,product); i++) {
                        workStation.pickOrder(o, product);
                    }


                    inOrder = o.getOpenProducts();

                }
            } catch (NoSuchProductException | ProductNotAssignedException | OrderAlreadyStartedException |
                     NoEmptyOrderSlotAvailableException | DuplicateProductException |
                     NoEmptyProductSlotAvailableException | OrderNotActiveException | NoSuchProductInOrderException |
                     NoSuchProductInOrderLeftException e) {
                throw new RuntimeException(e.getClass().getSimpleName() + ": " + workStation.getActiveOrders() + ",prd: " + workStation.getAssignedProducts());
            }
        });


    }
    private int getNumInOrder(Order o, Product p){
        AtomicInteger i = new AtomicInteger();
        o.getOpenProducts().forEach(product -> {
            if (product.equals(p)){
                i.getAndIncrement();
            }
        });
        return i.get();
    }
    private static boolean contains(Product[] all, Product p){
        return Arrays.stream(all).toList().contains(p);
    }
    private Product[] getOptimalPrds() {
        Product[] optimalPrds = new Product[4];
        Map<Product, Integer> prdNumMap = new HashMap<>();
        workStation.getActiveOrders().forEach(o -> o.getOpenProducts().forEach(p -> {
            if (!prdNumMap.containsKey(p)) {
                prdNumMap.put(p, 1);
            } else {
                prdNumMap.put(p, prdNumMap.get(p) + 1);
            }
        }));
        Map<Integer, Product> rev = new HashMap<>();
        for (int i = 0; i < prdNumMap.size(); i++) {
            rev.put(prdNumMap.get(new ArrayList<>(prdNumMap.keySet()).get(i)), new ArrayList<>(prdNumMap.keySet()).get(i));
        }
        ArrayList<Integer> sortedKeys = new ArrayList<>(rev.keySet());

        Collections.sort(sortedKeys);


        for (int i = 0; i < 4; i++) {
            optimalPrds[i] = rev.get(sortedKeys.get(i));
        }
        return optimalPrds;
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

