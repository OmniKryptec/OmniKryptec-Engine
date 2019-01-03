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

package de.omnikryptec.old.test;

import de.omnikryptec.old.util.logger.Logger;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

/**
 *
 * @author Panzer1119
 */
public class VisualThreadMonitor {

    private final ArrayList<StackTraceElement[]> stackTraces = new ArrayList<StackTraceElement[]>() {

	/**
	 * 
	 */
	private static final long serialVersionUID = -36832397820729877L;

	@Override
	public boolean contains(Object o) {
	    if (o instanceof StackTraceElement[]) {
		final StackTraceElement[] toTest = (StackTraceElement[]) o;
		boolean contains = false;
		for (StackTraceElement[] ste : this) {
		    if (ste.length == toTest.length) {
			boolean good = true;
			for (int i = 0; i < toTest.length; i++) {
			    if (!ste[i].toString().equalsIgnoreCase(toTest[i].toString())) {
				good = false;
				break;
			    }
			}
			if (good) {
			    contains = true;
			    break;
			}
		    }
		}
		return contains;
	    } else {
		return false;
	    }
	}

    };
    private DefaultMutableTreeNode top = new DefaultMutableTreeNode(null);
    private final JFrame frame = new JFrame();
    private final JTree tree = new JTree(top);
    private final JScrollPane scrollPane = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    private Thread thread;

    private final Thread thread_updater = new Thread(() -> {
	try {
	    while (true) {
		final StackTraceElement[] ste = thread.getStackTrace();
		if (ste.length == 0) {
		    continue;
		}
		if (!stackTraces.contains(ste)) {
		    stackTraces.add(ste);
		    final StackTraceElement[] iste = invertArray(ste);
		    DefaultMutableTreeNode node = top;
		    boolean first = true;
		    for (StackTraceElement e : iste) {
			if (first && node == top) {
			    node.setUserObject(e.toString());
			    first = false;
			} else {
			    final DefaultMutableTreeNode n = new DefaultMutableTreeNode(e.toString() + " | "
				    + LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())
					    .format(DateTimeFormatter.ofPattern("HH:mm:ss:SSSS")));
			    node.add(n);
			    node = n;
			    /*
			     * DefaultMutableTreeNode n = getChildNode(node, e.toString()); if(n == null) {
			     * n = new DefaultMutableTreeNode(e.toString()); node.add(n); } else if(n ==
			     * top) { top.setUserObject(e.toString()); }
			     */
			}
		    }
		    tree.revalidate();
		    tree.repaint();
		    updateTitle();
		}
	    }
	} catch (Exception ex) {
	    Logger.logErr("Error while monitoring thread: " + ex, ex);
	}
    });

    public VisualThreadMonitor(Thread thread) {
	this.thread = thread;
	frame.setLocationRelativeTo(null);
	frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	frame.setSize(new Dimension(600, 600));
	frame.setLayout(new BorderLayout());
	frame.add(scrollPane, BorderLayout.CENTER);
	updateTitle();
    }

    public void start() {
	stackTraces.clear();
	top = new DefaultMutableTreeNode(null);
	tree.setModel(new DefaultTreeModel(top));
	tree.revalidate();
	tree.repaint();
	frame.setVisible(true);
	thread_updater.start();
    }

    public void stop() {
	thread_updater.interrupt();
    }

    private void updateTitle() {
	frame.setTitle(String.format("%s - %s - %s", this.getClass().getSimpleName(), thread.getName(),
		(thread.isAlive() ? "Alive" : "Dead")));
    }

    public static <T> T[] invertArray(T[] array) {
	final T[] newArray = Arrays.copyOf(array, array.length);
	for (int i = 0; i < newArray.length; i++) {
	    newArray[i] = array[array.length - i - 1];
	    newArray[array.length - i - 1] = array[i];
	}
	return newArray;
    }

    private static DefaultMutableTreeNode getChildNode(DefaultMutableTreeNode node, Object userObject) {
	if (userObject == null || node == null || node.getUserObject() == null) {
	    return node;
	} else if (node.getUserObject().equals(userObject.toString())) {
	    return node;
	}
	Enumeration e = node.children();
	while (e.hasMoreElements()) {
	    DefaultMutableTreeNode n = (DefaultMutableTreeNode) e.nextElement();
	    if (n.getUserObject().equals(userObject.toString())) {
		return n;
	    }
	}
	return null;
    }

    private static DefaultMutableTreeNode getNode(DefaultMutableTreeNode node, Object userObject) {
	if (userObject == null || node == null || node.getUserObject() == null) {
	    return node;
	} else if (node.getUserObject().equals(userObject.toString())) {
	    return node;
	}
	Enumeration e = node.children();
	while (e.hasMoreElements()) {
	    DefaultMutableTreeNode n = (DefaultMutableTreeNode) e.nextElement();
	    if (n.getUserObject().equals(userObject.toString())) {
		return n;
	    } else {
		DefaultMutableTreeNode n_ = getNode(n, userObject);
		if (n_ != null) {
		    return n_;
		}
	    }
	}
	return null;
    }
}
