package dotpreviewer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Stack;

public class DotFilePreviewer
{
	public static void displayGraph (File dotfile) throws IOException {
		System.out.println("generating preview...");
		Runtime rt = Runtime.getRuntime();
		String[] commands = {"dot","-Tpng",dotfile.getAbsolutePath()};
		InputStream in = Runtime.getRuntime().exec(commands).getInputStream();
		BufferedImage preview = ImageIO.read(in);
		in.close();
		displayImage(preview);
	}

	public static void displayGraph (String[] lines) throws IOException {
		System.out.println("generating preview...");
		Runtime rt = Runtime.getRuntime();
		String[] commands = {"dot", "-Tpng"};
		ProcessBuilder pb = new ProcessBuilder(commands);
		Process proc = pb.start();
		InputStream in = proc.getInputStream();
		Writer out = proc.outputWriter();
		for (String s : lines) {
			out.write(s + "\n");
			System.out.println(s);
		}
		out.flush();
		BufferedImage preview = ImageIO.read(in);
		in.close();
		out.close();
		proc.destroy();
		displayImage(preview);
	}

	public static void displayImage (Image image) {
		int width = image.getWidth(null);
		int height = image.getHeight(null);
		double aspectRatio = (double)width/(double)height;
		Image scaledImage = image.getScaledInstance(
				height > width ? (int)(500*aspectRatio) : 500,
				width > height ? (int)(500/aspectRatio) : 500,
				Image.SCALE_SMOOTH
		);
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new GridLayout());
		frame.getContentPane().add(new JLabel(new ImageIcon(scaledImage)));
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void runInput () throws Throwable {
		System.out.println("enter the .dot graph:");
		ArrayList<String> lines = new ArrayList<String>();
		Scanner in = new Scanner(System.in);
		String preString = "";
		String current = "";
		int openBrackets = 0;
		do {
			System.out.print(preString);
			current = in.nextLine();
			if (current.contains("}")) {
				preString = preString.substring(0, preString.length() - 1);
				openBrackets--;
			}
			lines.add(preString + current);
			if (current.contains("{")) {
				preString += "\t";
				openBrackets++;
			}
		} while (openBrackets > 0);
		displayGraph(lines.toArray(new String[lines.size()]));
	}

	public static void main (String[] args) throws Throwable {
		if (args.length > 0) { // read from a file
			File dotFile = new File(args[0]);
			if (!dotFile.exists())
				throw new FileNotFoundException();
			displayGraph(dotFile);
			return;
		}
		// write it in-place
		runInput();
	}
}
