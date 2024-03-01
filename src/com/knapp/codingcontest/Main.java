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

package com.knapp.codingcontest;

import com.knapp.codingcontest.core.InfoSnapshotInternal;
import com.knapp.codingcontest.core.InputDataInternal;
import com.knapp.codingcontest.core.PrepareUpload;
import com.knapp.codingcontest.core.WarehouseInternal;
import com.knapp.codingcontest.operations.CostFactors;
import com.knapp.codingcontest.operations.InfoSnapshot;
import com.knapp.codingcontest.solution.Solution;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ----------------------------------------------------------------------------
 * you may change any code you like
 * => but changing the output may lead to invalid results on upload!
 * ----------------------------------------------------------------------------
 */
public class Main {
    // ----------------------------------------------------------------------------

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

    public static void main(final String... args) throws Exception {
        System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
        System.out.println("vvv   KNAPP Coding Contest: STARTING...        vvv");
        System.out.printf("vvv                %s                    vvv%n", Main.DATE_FORMAT.format(new Date()));
        System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");

        System.out.println("# ------------------------------------------------");
        System.out.println("# ... LOADING INPUT ...");
        final CostFactors costFactors = new MainCostFactors();
        final InputDataInternal iinput = new InputDataInternal(costFactors);
        final WarehouseInternal iwarehouse = new WarehouseInternal(iinput);
        iinput.readData(iwarehouse);
        final InputDataInternal.InputStat istat = iinput.inputStat();

        System.out.println("# ------------------------------------------------");
        System.out.println("# ... RUN YOUR SOLUTION ...");
        final long start = System.currentTimeMillis();
        final Solution solution = new Solution(iwarehouse, iinput);
        Throwable throwable = null;
        try {
            solution.run();
        } catch (final Throwable _throwable) {
            throwable = _throwable;
        }
        iwarehouse.finishAssignmentStats();
        final long end = System.currentTimeMillis();
        System.out.println("# ... DONE ... (" + Main.formatInterval(end - start) + ")");

        System.out.println("# ------------------------------------------------");
        System.out.println("# ... WRITING OUTPUT/RESULT ...");
        PrepareUpload.createZipFile(solution, iwarehouse);
        System.out.println(">>> Created " + PrepareUpload.FILENAME_RESULT + " & " + PrepareUpload.uploadFileName(solution));

        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        System.out.println("^^^   KNAPP Coding Contest: FINISHED           ^^^");
        System.out.printf("^^^                %s                    ^^^%n", Main.DATE_FORMAT.format(new Date()));
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

        System.out.println("# ------------------------------------------------");
        System.out.println("# ... RESULT/COSTS FOR YOUR SOLUTION ...");
        System.out.println("#     " + solution.getParticipantName() + " / " + solution.getParticipantInstitution());

        if (throwable != null) {
            System.out.println();
            System.out.println("# ... Ooops ...");
            System.out.println();
            throwable.printStackTrace(System.out);
        }

        Main.printResults(solution, istat, iwarehouse);
    }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    @SuppressWarnings("boxing")
    public static String formatInterval(final long interval) {
        final int h = (int) ((interval / (1000 * 60 * 60)) % 60);
        final int m = (int) ((interval / (1000 * 60)) % 60);
        final int s = (int) ((interval / 1000) % 60);
        final int ms = (int) (interval % 1000);
        return String.format("%02d:%02d:%02d.%03d", h, m, s, ms);
    }

    // ----------------------------------------------------------------------------

    @SuppressWarnings("boxing")
    public static void printResults(final Solution solution, final InputDataInternal.InputStat istat, final WarehouseInternal iwarehouse) throws Exception {
        final InfoSnapshotInternal info = iwarehouse.getInfoSnapshot();
        final CostFactors c = iwarehouse.getCostFactors();

        //
        final int os = info.getOperationCount(InfoSnapshot.OperationType.StartOrder);
        final int oa = info.getOperationCount(InfoSnapshot.OperationType.AssignProduct);
        final int or = info.getOperationCount(InfoSnapshot.OperationType.RemoveProduct);
        final int op = info.getOperationCount(InfoSnapshot.OperationType.PickOrder);

        //
        final int up = info.getUnfinishedProductCount();
        final double c_uo_ = info.getUnfinishedOrdersCost();
        final double c_a = info.getProductAssignmentCost();

        final double c_t = info.getTotalCost();

        //
        final long mo = info.getMissedOrders();
        final long mp = info.getMissedProducts();

        //
        System.out.println("# ------------------------------------------------");
        System.out.println("# ... RESULT/COSTS FOR YOUR SOLUTION ...");
        System.out.println("#     " + solution.getParticipantName() + " / " + solution.getParticipantInstitution());

        //
        System.out.println("  --------------------------------------------------------------");
        System.out.println("    INPUT STATISTICS                                            ");
        System.out.println("  ------------------------------------- : ----------------------");
        System.out.printf("      #work-station                     :  %8d%n", istat.countWorkStations);
        System.out.printf("      #orders                           :  %8d%n", istat.countOrders);
        System.out.printf("      #products                         :  %8d%n", istat.countProducts);
        System.out.printf("      #products (unique)                :  %8d%n", istat.countProductCodes);
        System.out.printf("      products / order                  :  %10.1f%n", istat.avgProductPerOrder);

        //
        System.out.println("  --------------------------------------------------------------");
        System.out.println("    RESULT STATISTICS                                           ");
        System.out.println("  ------------------------------------- : ----------------------");
        System.out.println("    #operations");
        System.out.printf("      #start order                      :  %8d%n", os);
        System.out.printf("      #assign product                   :  %8d%n", oa);
        System.out.printf("      #remove product                   :  %8d%n", or);
        System.out.printf("      #pick order/product               :  %8d%n", op);
        System.out.println("    missed opportunities");
        System.out.printf("      missed orders                     :  %8d%n", mo);
        System.out.printf("      missed products                   :  %8d%n", mp);

        //
        System.out.println("  =============================================================================");
        System.out.println("    RESULTS                                                                    ");
        System.out.println("  ===================================== : ============ | ======================");
        System.out.println("      what                              :       costs  |  (details: count,...)");
        System.out.println("  ------------------------------------- : ------------ | ----------------------");
        System.out.printf("   -> costs/unfinished products         :  %10.1f  |   %8d%n", c_uo_, up);
        System.out.printf("   -> costs assign/remove               :  %10.1f  |   %8d%n", c_a, (oa + or));
        System.out.println("  ------------------------------------- : ------------ | ----------------------");
        System.out.printf("   => TOTAL COST                           %10.1f%n", c_t);
        System.out.println("                                          ============");
    }

    // ----------------------------------------------------------------------------
}
