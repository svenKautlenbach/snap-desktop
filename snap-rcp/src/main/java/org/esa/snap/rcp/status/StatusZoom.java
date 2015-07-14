package org.esa.snap.rcp.status;

import com.bc.ceres.glayer.support.ImageLayer;
import com.bc.ceres.glayer.swing.LayerCanvas;
import org.esa.snap.framework.ui.PixelPositionListener;
import org.esa.snap.netbeans.docwin.DocumentWindowManager;
import org.esa.snap.rcp.windows.ProductSceneViewTopComponent;
import org.openide.awt.StatusLineElementProvider;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;

/**
 * @author muhammad.bc on 7/8/2015.
 * To add the level of band on the status bar.
 */
@ServiceProvider(service = StatusLineElementProvider.class, position = 30)
public class StatusZoom implements StatusLineElementProvider, DocumentWindowManager.Listener, PixelPositionListener {

    private final Dimension dimension;
    private final JSeparator separator;
    private final JLabel label;
    private final JPanel panel;

    public StatusZoom() {
        DocumentWindowManager.getDefault().addListener(this);
        separator = new JSeparator(SwingConstants.VERTICAL);
        dimension = new Dimension();
        label = new JLabel();
        dimension.setSize(100, 20);
        label.setPreferredSize(dimension);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.add(Box.createHorizontalGlue());
        panel.add(separator);
        dimension.setSize(100, 20);
        panel.setPreferredSize(dimension);
        panel.add(label);
    }

    @Override
    public void pixelPosChanged(ImageLayer imageLayer,
                                int pixelX,
                                int pixelY,
                                int currentLevel,
                                boolean pixelPosValid,
                                MouseEvent e) {
        if (pixelPosValid) {
            LayerCanvas layerCanvas = (LayerCanvas) e.getSource();
            double zoomFactor = layerCanvas.getViewport().getZoomFactor();
            String scaleStr;
            if (zoomFactor > 1.0) {
                double v = Math.round(10.0 * zoomFactor) / 10.0;
                scaleStr = ((int) v == v ? (int) v : v) + ":1";
            } else {
                double v = Math.round(10.0 / zoomFactor) / 10.0;
                scaleStr = "1:" + ((int) v == v ? (int) v : v);
            }
            label.setForeground(Color.black);
            label.setText(String.format("Zoom=%s", scaleStr));
        } else {
            label.setForeground(Color.red);
        }
    }

    @Override
    public void pixelPosNotAvailable() {
        label.setForeground(Color.RED);
    }


    @Override
    public void windowOpened(DocumentWindowManager.Event e) {
        TopComponent topComponent = e.getDocumentWindow().getTopComponent();
        if (topComponent instanceof ProductSceneViewTopComponent) {
            ProductSceneViewTopComponent component = (ProductSceneViewTopComponent) topComponent;
            component.getView().addPixelPositionListener(this);
        }
    }

    @Override
    public void windowClosed(DocumentWindowManager.Event e) {
        TopComponent topComponent = e.getDocumentWindow().getTopComponent();
        if (topComponent instanceof ProductSceneViewTopComponent) {
            ProductSceneViewTopComponent component = (ProductSceneViewTopComponent) topComponent;
            component.getView().removePixelPositionListener(this);
        }
    }

    @Override
    public void windowSelected(DocumentWindowManager.Event e) {

    }

    @Override
    public void windowDeselected(DocumentWindowManager.Event e) {

    }

    @Override
    public Component getStatusLineElement() {
        return panel;
    }
}