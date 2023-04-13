package com.AntonSibgatulin.location;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Timer {
	public int reload = 0;
	public int count = 0;
	public javax.swing.Timer timer = null;
	public int i = 0;

	public Timer(final int reload, final int count, final ActionListener actionListener,
			final ActionListener actionListener2) {
		this.count = count;
		this.reload = reload;

		timer = new javax.swing.Timer(reload / count, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				actionListener.actionPerformed(e);
				i++;
				if (i > count - 1) {
					actionListener2.actionPerformed(e);
					stop();
				}

			}
		});

	}

	public void start() {
		i = 0;
		timer.start();
	}

	public void stop() {
		timer.stop();
	}
}
