package com.ois.scanning.util;

import android.app.Activity;

public interface Action<A extends Activity> {
	void run(A activity);
}
