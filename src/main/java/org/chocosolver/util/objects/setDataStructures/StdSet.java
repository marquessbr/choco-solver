/**
 * This file is part of choco-solver, http://choco-solver.org/
 *
 * Copyright (c) 2017, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.util.objects.setDataStructures;

import org.chocosolver.memory.IEnvironment;
import org.chocosolver.memory.structure.IOperation;
import org.chocosolver.util.PoolManager;

/**
 * Generic backtrable set for trailing
 *
 * @author Jean-Guillaume Fages
 * @since Nov 2012
 */
public class StdSet implements ISet {

	//***********************************************************************************
	// VARIABLES
	//***********************************************************************************

    // trailing
    private final IEnvironment environment;
    private PoolManager<ListOP> operationPoolGC;
    private final static boolean ADD = true;
    private final static boolean REMOVE = false;
    // set (decorator design pattern)
    private ISet set;

	//***********************************************************************************
	// CONSTRUCTOR
	//***********************************************************************************

    public StdSet(IEnvironment environment, ISet set) {
        super();
        this.environment = environment;
        this.operationPoolGC = new PoolManager<>();
        this.set = set;
    }

	//***********************************************************************************
	// METHODS
	//***********************************************************************************

    @Override
    public ISetIterator newIterator() {
        return set.newIterator();
    }

	@Override
    public ISetIterator iterator(){
		return set.iterator();
	}

    @Override
    public boolean add(int element) {
        if (set.add(element)) {
            ListOP op = operationPoolGC.getE();
            if (op == null) {
                op = new ListOP();
            }
            op.set(element, REMOVE);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(int element) {
        if (set.remove(element)) {
            ListOP op = operationPoolGC.getE();
            if (op == null) {
                op = new ListOP();
            }
            op.set(element, ADD);
            return true;
        }
        return false;
    }

    @Override
    public boolean contains(int element) {
        return set.contains(element);
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public void clear() {
        ISetIterator iter = iterator();
        while (iter.hasNext()) {
            ListOP op = operationPoolGC.getE();
            if (op == null) {
                op = new ListOP();
            }
            op.set(iter.nextInt(), ADD);
        }
        set.clear();
    }

    @Override
   	public int min() {
   		return set.min();
   	}

   	@Override
   	public int max() {
   		return set.max();
   	}

    @Override
    public String toString() {
        return set.toString();
    }

    //***********************************************************************************
    // TRAILING OPERATIONS
    //***********************************************************************************

    private class ListOP implements IOperation {
        private int element;
        private boolean addOrRemove;

        @Override
        public void undo() {
            if (addOrRemove) {
                set.add(element);
            } else {
                set.remove(element);
            }
            operationPoolGC.returnE(this);
        }

        public void set(int i, boolean add) {
            element = i;
            addOrRemove = add;
            environment.save(this);
        }
    }

	@Override
	public SetType getSetType(){
		return set.getSetType();
	}
}
