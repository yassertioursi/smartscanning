package com.ois.scanning.camera;

import android.graphics.PointF;

public interface ICameraOverlay {
	void showCorners(boolean shown);
	void showAlert(boolean alert, int delay);

	void setDocumentCorners(PointF[] points);
}
