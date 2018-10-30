/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.smsd.mcs;

import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.AtomMatcher;
import org.openscience.cdk.isomorphism.BondMatcher;
import org.openscience.cdk.isomorphism.Mappings;
import org.openscience.cdk.isomorphism.VentoFoggia;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.smsd.Isomorphism;
import org.openscience.smsd.Substructure;
import org.openscience.smsd.helper.MoleculeInitializer;
import org.openscience.smsd.interfaces.Algorithm;

/**
 *
 * @author Syed Asad Rahman <asad at ebi.ac.uk>
 */
public class MCSTest {

    /**
     * Test of set method, of class MCSPlusHandler.
     *
     * @throws Exception
     */
    @Test
    public void testSet_IMolecule_IMolecule() throws Exception {
        ////////System.out.println("3");
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer query = sp.parseSmiles("Nc1ccccc1");
        IAtomContainer target = sp.parseSmiles("Nc1ccccc1");

        MoleculeInitializer.initializeMolecule(query);
        MoleculeInitializer.initializeMolecule(target);

        org.openscience.smsd.algorithm.mcsplus1.MCSPlus mcs
                = new org.openscience.smsd.algorithm.mcsplus1.MCSPlus(query, target, true, true, true);
        mcs.search_cliques();
        List<List<Integer>> overlaps = mcs.getFinalMappings();
        assertEquals(overlaps.size(), 2);
        assertEquals(7, (mcs.getFinalMappings().iterator().next().size() / 2));
    }

    /**
     * Test of set method, of class MCSPlusHandler.
     *
     * @throws Exception
     */
    @Test
    public void testSet_count() throws Exception {
        ////////System.out.println("3");
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer query = sp.parseSmiles("*C1OC(CO)C(OP=O)C1O");
        IAtomContainer target = sp.parseSmiles("*[P](=O)(O)OCC1O[CH]C(OC(=O)C(N)CC2=CC=C(O)C=C2)C1O");

        MoleculeInitializer.initializeMolecule(query);
        MoleculeInitializer.initializeMolecule(target);

        org.openscience.smsd.algorithm.mcsplus1.MCSPlus mcs
                = new org.openscience.smsd.algorithm.mcsplus1.MCSPlus(query, target, true, false, false);
        mcs.search_cliques();
        List<List<Integer>> overlaps = mcs.getFinalMappings();
        assertEquals(overlaps.size(), 1);
        assertEquals(9, (mcs.getFinalMappings().iterator().next().size() / 2));
    }

    @Test
    public void test_VFMCS_count() throws Exception {
        ////////System.out.println("3");
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer query = sp.parseSmiles("*C1OC(CO)C(OP=O)C1O");
        IAtomContainer target = sp.parseSmiles("*[P](=O)(O)OCC1O[CH]C(OC(=O)C(N)CC2=CC=C(O)C=C2)C1O");

//        IAtomContainer query = sp.parseSmiles("CC1=C(CCC([O-])=O)C2=[N+]3C1=Cc1c(C)c(C=C)c4C=C5C(C)=C(C=C)C6=[N+]5[Fe-]3(n14)n1c(=C6)c(C)c(CCC([O-])=O)c1=C2");
//        IAtomContainer target = sp.parseSmiles("C1=2N3C(C=C4[N+]5=C(C=C6N7C8=CC9=[N+](C(=C1)C(=C9CCC([O-])=O)C)[Fe-2]573OC(CCC=%10C=%11C=C%12C(=C(C%13=CC%14=[N+]%15C(=CC=%16N%17C(=C(C%16C=C)C)C=C([N+]%11[Fe-2]%15%17(N%13%12)OC(CCC8=C6C)=O)C%10C)C(=C%14C=C)C)C)CCC([O-])=O)=O)C(=C4C)C=C)=C(C2C)C=C");
//
        MoleculeInitializer.initializeMolecule(query);
        MoleculeInitializer.initializeMolecule(target);

        Isomorphism mcs
                = new Isomorphism(query, target, Algorithm.VFLibMCS, true, true, true);
//                = new Isomorphism(query, target, Algorithm.VFLibMCS, false, false, false);

        System.out.println("mcs.getFirstAtomMapping() " + mcs.getFirstAtomMapping());
//        List<AtomAtomMapping> allAtomMapping = mcs.getAllAtomMapping();
        // assertEquals(2, allAtomMapping.size());
        assertEquals(9, mcs.getFirstAtomMapping().getCount());
    }

    @Test
    public void test_Pattern_count() throws Exception {
        ////////System.out.println("3");
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());

        IAtomContainer query = sp.parseSmiles("C1=CC=CC=C1");
        IAtomContainer target = sp.parseSmiles("OC1=CC=CC=C1");

        MoleculeInitializer.initializeMolecule(query);
        MoleculeInitializer.initializeMolecule(target);

        AtomMatcher am;
        BondMatcher bm;
        boolean matchBonds = true;
        boolean matchAtom = true;

        if (matchAtom) {
            am = AtomMatcher.forQuery();
        } else {
            am = AtomMatcher.forElement();
        }

        if (matchBonds) {
            bm = BondMatcher.forOrder();
        } else {
            bm = BondMatcher.forAny();
        }

        boolean flagSubGraph = false;
        if (query.getAtomCount() >= target.getAtomCount()) {

            org.openscience.cdk.isomorphism.Pattern pattern = VentoFoggia.findSubstructure(target, am, bm); // create pattern
            Mappings limit = pattern.matchAll(query).limit(1);
            flagSubGraph = limit.count() > 0;
        }
        if (query.getAtomCount() < target.getAtomCount()) {
            org.openscience.cdk.isomorphism.Pattern pattern
                    = VentoFoggia.findSubstructure(query, AtomMatcher.forElement(), BondMatcher.forOrder()); // create pattern
            Mappings limit = pattern.matchAll(target).limit(1);
            flagSubGraph = limit.count() > 0;
        }
        assertEquals(true, flagSubGraph);

    }

    @Test
    public void test_Substructure_count() throws Exception {
        ////////System.out.println("3");
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer query = sp.parseSmiles("C1=CC=CC=C1");
        IAtomContainer target = sp.parseSmiles("OC1=CC=CC=C1");

        MoleculeInitializer.initializeMolecule(query);
        MoleculeInitializer.initializeMolecule(target);

        org.openscience.smsd.Substructure sub = new org.openscience.smsd.Substructure(query, target, true, true, false, false);
        assertEquals(true, sub.isSubgraph());

    }

    @Test
    public void test_Identical_count() throws Exception {
        ////////System.out.println("3");
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());;

        IAtomContainer query = sp.parseSmiles("C1=CC=CC=C1");
        IAtomContainer target = sp.parseSmiles("C1=CC=CC=C1");

        MoleculeInitializer.initializeMolecule(query);
        MoleculeInitializer.initializeMolecule(target);

        org.openscience.smsd.Substructure sub = new org.openscience.smsd.Substructure(query, target, true, true, false, false);
        assertEquals(true, sub.isSubgraph());

    }

    @Test
    public void test_MCSPlusBK_count() throws Exception {
        ////////System.out.println("3");
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());

//        IAtomContainer query = sp.parseSmiles("cccc");
//        IAtomContainer target = sp.parseSmiles("ccnc");
//        IAtomContainer query = sp.parseSmiles("C1=CC=CC=C1");
//        IAtomContainer target = sp.parseSmiles("C1=CC=CC=C1");
        IAtomContainer query = sp.parseSmiles("C1OC(CO)C(OP=O)C1O");
        IAtomContainer target = sp.parseSmiles("[P](=O)(O)OCC1O[CH]C(OC(=O)C(N)CC2=CC=C(O)C=C2)C1O");
//        IAtomContainer query = sp.parseSmiles("CC1=C(CCC([O-])=O)C2=[N+]3C1=Cc1c(C)c(C=C)c4C=C5C(C)=C(C=C)C6=[N+]5[Fe-]3(n14)n1c(=C6)c(C)c(CCC([O-])=O)c1=C2");
//        IAtomContainer target = sp.parseSmiles("C1=2N3C(C=C4[N+]5=C(C=C6N7C8=CC9=[N+](C(=C1)C(=C9CCC([O-])=O)C)[Fe-2]573OC(CCC=%10C=%11C=C%12C(=C(C%13=CC%14=[N+]%15C(=CC=%16N%17C(=C(C%16C=C)C)C=C([N+]%11[Fe-2]%15%17(N%13%12)OC(CCC8=C6C)=O)C%10C)C(=C%14C=C)C)C)CCC([O-])=O)=O)C(=C4C)C=C)=C(C2C)C=C");
        MoleculeInitializer.initializeMolecule(query);
        MoleculeInitializer.initializeMolecule(target);

        try {
            org.openscience.smsd.algorithm.mcsplus2.MCSPlusMapper sub
                    = new org.openscience.smsd.algorithm.mcsplus2.MCSPlusMapper(query, target, false, false, false);
            //System.out.println("MCS " + (sub.getFirstAtomMapping().getCommonFragmentAsSMILES()));
            assertEquals("C1OC(CO)C(O)C1O", sub.getFirstAtomMapping().getCommonFragmentAsSMILES());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Test
//    public void test_MCSLarge_count() throws Exception {
//        ////////System.out.println("3");
//        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
//
//        IAtomContainer query = sp.parseSmiles("[H]O[C@H]1C[C@H]([*])O[C@@H]1COP(O)(=O)O[C@H]1C[C@H]2O[C@@H]1COP(O)(=O)O[C@H]1C[C@@H](O[C@@H]1COP(O)(=O)O[C@H]1C[C@H]([*])O[C@@H]1COP(O)(O)=O)N1C(=O)NC(=O)[C@@]3(C)[C@]1([H])[C@@]1([H])N2C(=O)NC(=O)[C@@]31C");
//        IAtomContainer target = sp.parseSmiles("[H]O[C@H]1C[C@H]([*])O[C@@H]1COP(O)(=O)O[C@H]1C[C@H]2O[C@@H]1COP(O)(=O)O[C@H]1C[C@@H](O[C@@H]1COP(O)(=O)O[C@H]1C[C@H]([*])O[C@@H]1COP(O)(O)=O)N1C(=O)NC(=O)[C@@]3(C)[C@]1([H])[C@@]1([H])N2C(=O)NC(=O)[C@@]31C");
//
//        MoleculeInitializer.initializeMolecule(query);
//        MoleculeInitializer.initializeMolecule(target);
//
//        try {
//            org.openscience.smsd.algorithm.mcsplus.MCSPlusMapper sub
//                    = new org.openscience.smsd.algorithm.mcsplus.MCSPlusMapper(query, target, false, false, false);
////            System.out.println("MCS " + (sub.getFirstAtomMapping().getCount()));
////            System.out.println("MCS " + (sub.getFirstAtomMapping().getCommonFragmentAsSMILES()));
//            assertEquals(68, sub.getFirstAtomMapping().getCount());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            org.openscience.smsd.algorithm.vflib.VF2MCS sub = new org.openscience.smsd.algorithm.vflib.VF2MCS(query, target, false, false, false);
////            System.out.println("MCS " + (sub.getFirstAtomMapping().getCount()));
////            System.out.println("MCS " + (sub.getFirstAtomMapping().getCommonFragmentAsSMILES()));
//            assertEquals(68, sub.getFirstAtomMapping().getCount());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Test
    public void test_MCSPlusBig_count() throws Exception {
        ////////System.out.println("3");
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());

        IAtomContainer query = sp.parseSmiles("[H][C@@]1(O[C@@](C[C@@H](O)[C@H]1O)(O[C@@H]1C[C@@](OC[C@H]2O[C@@H](OC[C@H]3O[C@H](OP([O-])([O-])=O)[C@H](NC(=O)C[C@H](O)CCCCCCCCCCC)[C@@H](OC(=O)C[C@H](O)CCCCCCCCCCC)[C@@H]3O)[C@H](NC(=O)C[C@@H](CCCCCCCCCCC)OC(=O)CCCCCCCCCCC)[C@@H](OC(=O)C[C@@H](CCCCCCCCCCC)OC(=O)CCCCCCCCCCCCC)[C@@H]2OP([O-])([O-])=O)(O[C@]([H])([C@H](O)CO)[C@@H]1O[C@H]1O[C@H]([C@@H](O)CO)[C@@H](OP([O-])([O-])=O)[C@H](O[C@H]2O[C@H]([C@@H](O)CO[C@H]3O[C@H]([C@@H](O)CO)[C@@H](O)[C@H](O)[C@@H]3O)[C@@H](OP([O-])([O-])=O)[C@H](O[C@H]3O[C@H](CO[C@H]4O[C@H](CO)[C@H](O)[C@H](O)[C@H]4O)[C@@H](O)[C@H](O)[C@H]3O)[C@@H]2O)[C@@H]1O)C([O-])=O)C([O-])=O)[C@H](O)CO");
        IAtomContainer target = sp.parseSmiles("[H][C@@]1(O[C@@](C[C@@H](O)[C@H]1O)(O[C@@H]1C[C@@](OC[C@H]2O[C@@H](OC[C@H]3O[C@H](OP([O-])([O-])=O)[C@H](NC(=O)C[C@H](O)CCCCCCCCCCC)[C@@H](OC(=O)C[C@H](O)CCCCCCCCCCC)[C@@H]3O)[C@H](NC(=O)C[C@@H](CCCCCCCCCCC)OC(=O)CCCCCCCCCCC)[C@@H](OC(=O)C[C@@H](CCCCCCCCCCC)OC(=O)CCCCCCCCCCCCC)[C@@H]2OP([O-])([O-])=O)(O[C@]([H])([C@H](O)CO)[C@@H]1O[C@H]1O[C@H]([C@@H](O)CO)[C@@H](OP([O-])([O-])=O)[C@H](O[C@H]2O[C@H]([C@@H](O)CO[C@H]3O[C@H]([C@@H](O)CO)[C@@H](O)[C@H](O)[C@@H]3O)[C@@H](OP([O-])([O-])=O)[C@H](O[C@H]3O[C@H](CO[C@H]4O[C@H](CO)[C@H](O)[C@H](O)[C@H]4O)[C@@H](O)[C@H](O[C@H]4O[C@H](CO)[C@@H](O)[C@H](O)[C@H]4O)[C@H]3O)[C@@H]2O)[C@@H]1O)C([O-])=O)C([O-])=O)[C@H](O)CO");

        MoleculeInitializer.initializeMolecule(query);
        MoleculeInitializer.initializeMolecule(target);

        try {
//            System.out.println("CALLING Substructure");
            Substructure sub
                    = new Substructure(query, target, false, false, true, true);
            System.out.println("Subgraph " + sub.isSubgraph());
            //System.out.println("MCS " + (sub.getFirstAtomMapping().getCommonFragmentAsSMILES()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
