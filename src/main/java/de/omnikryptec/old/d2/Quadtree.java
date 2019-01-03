/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.old.d2;

import java.util.ArrayList;

public class Quadtree {

    private int MAX_OBJECTS = 10;

    private int level;
    private ArrayList<Rectangle> objects;
    private Rectangle bounds;
    private Quadtree[] nodes;
    private Quadtree parent;

    /*
     * Constructor
     */
    public Quadtree(int pLevel, Rectangle pBounds, Quadtree... pnodes) {
	level = pLevel;
	objects = new ArrayList<>();
	bounds = pBounds;
	nodes = pnodes;
    }

    public int getlevel() {
	return level;
    }

    @Override
    public String toString() {
	return "QT: " + bounds.toString();
    }

    public Quadtree(int plevel, Rectangle bo) {
	this(plevel, bo, new Quadtree[4]);
    }

    /*
     * Clears the quadtree
     */
    public void clear() {
	objects.clear();
	if (parent != null) {
	    Quadtree tmp = parent;
	    parent = null;
	    tmp.clear();
	}
	for (int i = 0; i < nodes.length; i++) {
	    if (nodes[i] != null) {
		nodes[i].clear();
		nodes[i] = null;
	    }
	}
    }

    /*
     * Splits the node into 4 subnodes
     */
    private void split() {
	float subWidth = (bounds.getWidth() / 2);
	float subHeight = (bounds.getHeight() / 2);
	float x = bounds.minX;
	float y = bounds.minY;
	if (nodes[0] == null) {
	    nodes[0] = new Quadtree(level + 1, new Rectangle(x + subWidth, y, subWidth, subHeight)).setParent(this);
	}
	if (nodes[1] == null) {
	    nodes[1] = new Quadtree(level + 1, new Rectangle(x, y, subWidth, subHeight)).setParent(this);
	}
	if (nodes[2] == null) {
	    nodes[2] = new Quadtree(level + 1, new Rectangle(x, y + subHeight, subWidth, subHeight)).setParent(this);
	}
	if (nodes[3] == null) {
	    nodes[3] = new Quadtree(level + 1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight))
		    .setParent(this);
	}
    }

    private void parentize(int newindex) {
	float width = bounds.getWidth() * 2;
	float height = bounds.getHeight() * 2;
	Quadtree[] array = new Quadtree[4];
	array[newindex] = this;
	Rectangle parentbounds = new Rectangle(0, 0, width, height);
	switch (newindex) {
	case 0:
	    parentbounds.minX = bounds.minX - bounds.getWidth();
	    parentbounds.minY = bounds.minY - bounds.getHeight();
	    break;
	case 1:
	    parentbounds.minX = bounds.minX;
	    parentbounds.minY = bounds.minY - bounds.getHeight();
	    break;
	case 2:
	    parentbounds.minX = bounds.minX;
	    parentbounds.minY = bounds.minY;
	    break;
	case 3:
	    parentbounds.minX = bounds.minX - bounds.getWidth();
	    parentbounds.minY = bounds.minY;
	    break;
	}
	parentbounds.setWidth(width).setHeight(height);
	setParent(new Quadtree(level - 1, parentbounds, array));
    }

    private Quadtree setParent(Quadtree tree) {
	this.parent = tree;
	return this;
    }

    /*
     * Determine which node the object belongs to. -1 means object cannot completely
     * fit within a child node and is part of the parent node
     */
    private int getIndex(Rectangle pRect) {
	int index = -1;
	double verticalMidpoint = bounds.minX + (bounds.getWidth() / 2);
	double horizontalMidpoint = bounds.minY + (bounds.getHeight() / 2);

	// Object can completely fit within the top quadrants
	boolean topQuadrant = (pRect.minY < horizontalMidpoint && pRect.minY + pRect.getHeight() < horizontalMidpoint);
	// Object can completely fit within the bottom quadrants
	boolean bottomQuadrant = (pRect.minY > horizontalMidpoint);

	// Object can completely fit within the left quadrants
	if (pRect.minX < verticalMidpoint && pRect.minX + pRect.getWidth() < verticalMidpoint) {
	    if (topQuadrant) {
		index = 1;
	    } else if (bottomQuadrant) {
		index = 2;
	    }
	}
	// Object can completely fit within the right quadrants
	else if (pRect.minX > verticalMidpoint) {
	    if (topQuadrant) {
		index = 0;
	    } else if (bottomQuadrant) {
		index = 3;
	    }
	}

	return index;
    }

    private boolean outOfBounds(Rectangle rect) {
	return rect.maxX > bounds.maxX || rect.maxY > bounds.maxY || rect.minX < bounds.minX || rect.minY < bounds.minY;
    }

    private int getIn(Rectangle r) {
	boolean bottomInd = r.maxY > bounds.maxY;
	boolean rightInd = r.minX < bounds.minX;
	int index = 0;
	if (bottomInd) {
	    if (rightInd) {
		index = 3;
	    } else {
		index = 2;
	    }
	} else {
	    if (rightInd) {
		index = 0;
	    } else {
		index = 1;
	    }
	}
	return index;
    }

    /*
     * Insert the object into the quadtree. If the node exceeds the capacity, it
     * will split and add all objects to their corresponding nodes.
     */
    public void insert(Rectangle pRect) {
	if (outOfBounds(pRect)) {
	    if (parent == null) {
		parentize(getIn(pRect));
	    } else {
		parent.insert(pRect);
	    }
	    return;
	}
	if (nodes[0] != null) {
	    int index = getIndex(pRect);

	    if (index >= 0 && nodes[index] != null) {
		nodes[index].insert(pRect);

		return;
	    }
	}

	objects.add(pRect);

	if (objects.size() > MAX_OBJECTS) {
	    split();
	    int i = 0;
	    while (i < objects.size()) {
		int index = getIndex(objects.get(i));
		if (index >= 0) {
		    nodes[index].insert(objects.remove(i));
		} else {
		    i++;
		}
	    }
	}
    }

    /*
     * Return all objects that could collide with the given object
     */
    public ArrayList<Rectangle> retrieve(ArrayList<Rectangle> returnObjects, Rectangle pRect) {
	int index = getIndex(pRect);
	if (index >= 0 && nodes[index] != null) {
	    nodes[index].retrieve(returnObjects, pRect);
	}
	returnObjects.addAll(objects);
	return returnObjects;
    }
}
