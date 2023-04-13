package com.AntonSibgatulin.services;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Base64;
import java.util.Random;

import javax.imageio.ImageIO;

public class CaptchaGenerateModel {
	public String[] data = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "r", "s",
			"t", "x", "y", "z", "w", "v", "q", "u", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0" };
	public final int MAX_LENGTH = 10;
	public String line = null;

	public String Generate(int length) {
		if (length >= MAX_LENGTH) {
			length = MAX_LENGTH;
		}
		int size = 40;
		BufferedImage bufferedImage = new BufferedImage(length * size + 2 * size, 3 * size, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bufferedImage.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
		g.setFont(new Font("Serif", Font.ITALIC, size));
		g.setColor(Color.white);
		line = "";
		for (int i = 0; i < length; i++) {
			int rand = new Random().nextInt(data.length);
			g.drawString("" + data[rand], 4 * new Random().nextInt(6) + i * size + 4 * i,
					size * (1 + new Random().nextInt(2)));
			line += data[rand];
		}
		for (int i = 0; i < 4 + new Random().nextInt(10); i++) {

			BasicStroke pen1 = new BasicStroke(1 + new Random().nextInt(2)); // толщина
																				// линии
																				// 20
			g.setStroke(pen1);
			g.drawLine(0, new Random().nextInt(size * 3), bufferedImage.getWidth(), (new Random().nextInt(size * 3)));
		}
		g.dispose();

		return "data:image/png;base64," + imgToBase64String(bufferedImage, "jpg");
	}

	public static String imgToBase64String(BufferedImage img, final String formatName) {
		final ByteArrayOutputStream os = new ByteArrayOutputStream();

		try {
			ImageIO.write(img, formatName, os);
			return Base64.getEncoder().encodeToString(os.toByteArray());
		} catch (final IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}

}
