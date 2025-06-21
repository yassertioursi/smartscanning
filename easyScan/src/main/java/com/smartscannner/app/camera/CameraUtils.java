package com.smartscanner.app.camera;

import android.hardware.Camera;

import com.smartscanner.app.BuildConfig;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class CameraUtils {

	public static int quantizeDegreeTo360(int angle, int quantum, int offset) {
		if (quantum == 0) {
			throw new IllegalArgumentException("Cannot quantize angle to 0");
		}
		angle = normalizeDegreeTo360(angle) + offset;
		return normalizeDegreeTo360(angle / quantum * quantum);
	}

	public static int normalizeDegreeTo360(int angle)
	{
		return ((angle % 360) + 360) % 360;
	}


	public static int normalizeDegreeTo180(int angle)
	{
		// force normalize to [0..180)
		angle = ((angle % 360) + 360) % 360;
		if (BuildConfig.DEBUG && !(angle >= 0)) {
			throw new AssertionError();
		}

		return angle - 360*((angle - 1)/180);
	}


	public static Map<Camera.Size, Integer> sortMapByValue(Map<Camera.Size, Integer> unsortedMap, final boolean order)
	{
		List<Map.Entry<Camera.Size, Integer>> list = new LinkedList<Map.Entry<Camera.Size, Integer>>(unsortedMap.entrySet());

		// Sorting the list based on values
		Collections.sort(list, new Comparator<Map.Entry<Camera.Size, Integer>>() {
			@Override
			public int compare(Map.Entry<Camera.Size, Integer> lhs, Map.Entry<Camera.Size, Integer> rhs) {
				if (order) {
					return lhs.getValue().compareTo(rhs.getValue());
				} else {
					return rhs.getValue().compareTo(lhs.getValue());
				}
			}
		});

		Map<Camera.Size, Integer> sortedMap = new LinkedHashMap<Camera.Size, Integer>();
		for (Map.Entry<Camera.Size, Integer> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	public static Map<Camera.Size, Integer> reverseMap(Map<Camera.Size, Integer> directMap)
	{
		List<Map.Entry<Camera.Size, Integer>> list = new LinkedList<Map.Entry<Camera.Size, Integer>>(directMap.entrySet());
		Collections.reverse(list);

		Map<Camera.Size, Integer> reverseMap = new LinkedHashMap<Camera.Size, Integer>();
		for (Map.Entry<Camera.Size, Integer> entry : list) {
			reverseMap.put(entry.getKey(), entry.getValue());
		}

		return reverseMap;
	}

}
