package jdraw.gui;

import java.awt.Point;
import java.util.ArrayList;

import Comandi.Comando;
import jdraw.data.Clip;
import jdraw.data.Frame;
import jdraw.gui.undo.DrawPixel;
import jdraw.gui.undo.UndoManager;
import util.Assert;
import util.Util;

/*
 * Created on 29-Oct-2003
 *
 * @author michaela
 */

public final class PixelTool extends Tool {

	public static final PixelTool INSTANCE = new PixelTool();

	private int lastButton;

	private ArrayList pixels = new ArrayList();
	private ArrayList oldColours = new ArrayList();

	private int currentColour;

	private PixelTool() {
	}

	public final void deactivate() {
		super.deactivate();
		pixels.clear();
		oldColours.clear();
	}
	
	private void definisciComando(Point p) {
		int dimensioneDisegno=Clip.getLarghezzaDisegno();
		int x =p.x-dimensioneDisegno;
		int y = Util.getYInvertita(p.y);
		Comando[][] matriceComandi= Clip.getMatriceComandi();
		Comando c;
		
		switch (x) {
		case 1:
			GuidafiloDialog d = new GuidafiloDialog();
			d.open();
			
			c =matriceComandi[y][x];
			if(d.getResult()==1) {
				c.setValue(d.getGuidafilo());
				matriceComandi[y][x]=c;
			}
			released(1, p);
			aggiornaValoriComandi(matriceComandi,1,c);
			break;
		case 2:
			GradazioneDialog g = new GradazioneDialog();
			g.open();
			
			c =matriceComandi[y][x];
			if(g.getResult()==1) {
				c.setValue(g.getGradazione());
				matriceComandi[y][x]=c;
			}
			released(1, p);
			aggiornaValoriComandi(matriceComandi,2,c);
			break;
		case 3:
			TirapezzaDialog t = new TirapezzaDialog();
			t.open();
			
			c =matriceComandi[y][x];
			if(t.getResult()==1) {
				c.setValue(t.getTirapezza());
				matriceComandi[y][x]=c;
			}
			released(1, p);
			aggiornaValoriComandi(matriceComandi,3,c);
			break;
		case 4:
			VelocitaDialog v = new VelocitaDialog();
			v.open();
			
			c =matriceComandi[y][x];
			if(v.getResult()==1) {
				c.setValue(v.getVelocita());
				matriceComandi[y][x]=c;
			}
			released(1, p);
			aggiornaValoriComandi(matriceComandi,4,c);
			break;
		case 5:
			break;

		default:
			break;
		}
	}
	
	private void aggiornaValoriComandi(Comando[][] matriceComandi, int indexComando,Comando c) {
		for(int i=0;i<matriceComandi.length;i++) {
			if(matriceComandi[i][indexComando]!=null && matriceComandi[i][indexComando].getColore()==c.getColore())
				matriceComandi[i][indexComando].setValue(c.getValue());
		}
		
	}

	public void pressed(int button, Point p) { // Cattura la pressione del mouse
		lastButton = button;
		if (p != null) {

			switch (button) {
				case LEFT_BUTTON :
					setPixel(p, Tool.getPicture().getForeground());
					break;
				case RIGHT_BUTTON :
					int dimensioneDisegno=Clip.getLarghezzaDisegno();
					if(p.x<dimensioneDisegno)
						setPixel(p, Tool.getPicture().getBackground());
					else {
						// tasto destro a destra del disegno
						System.out.println("Definisco comando");
						definisciComando(p);
					}
					break;
				default :
					Assert.fail("gui: unknown button " + button);
			}
		}
	}

	private void setPixel(Point p, int colour) {
		Frame frame = getCurrentFrame();
		
		int y_Invertita=Clip.getYInvertita(p.y);
		currentColour = colour;
			int oldColour = frame.getPixel(p.x,y_Invertita);
			p.setLocation(p.x, y_Invertita);
			pixels.add(p);
			oldColours.add(new Integer(oldColour));
			frame.setPixel(p.x, y_Invertita, colour);
	}

	public void released(int button, Point p) {
		final int size = pixels.size();
		if (size > 0) {
			Point[] points = new Point[size];
			pixels.toArray(points);

			int[] oldCols = new int[size];
			for (int i = 0; i < size; i++) {
				oldCols[i] = ((Integer) oldColours.get(i)).intValue();
			}
			DrawPixel dp = new DrawPixel(points, currentColour, oldCols);
			UndoManager.INSTANCE.addUndoable(dp);

			pixels.clear();
			oldColours.clear();
		}
	}

}
