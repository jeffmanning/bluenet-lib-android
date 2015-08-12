package nl.dobots.bluenet.extended.structs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Copyright (c) 2015 Dominik Egger <dominik@dobots.nl>. All rights reserved.
 * <p/>
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3, as
 * published by the Free Software Foundation.
 * <p/>
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 3 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 * <p/>
 * Created on 15-7-15
 *
 * @author Dominik Egger
 */
public class BleDeviceMap extends HashMap<String, BleDevice> {

	public enum SortOrder {
		ascending,
		descending
	}

	public BleDevice getDevice(String address) {
		return get(address);
	}

	public boolean contains(BleDevice device) {
		return containsKey(device.getAddress());
	}

	public BleDevice updateDevice(BleDevice device) {
		if (contains(device)) {
			BleDevice old = getDevice(device.getAddress());
			old.updateRssiValue(System.currentTimeMillis(), device.getRssi());
			return old;
		} else {
			put(device.getAddress(), device);
			return device;
		}
	}

	public BleDeviceList getList() {
		BleDeviceList result = new BleDeviceList();
		result.addAll(values());
		return result;
	}

	public BleDeviceList getRssiSortedList() {
		return getRssiSortedList(SortOrder.descending);
	}

	public BleDeviceList getRssiSortedList(final SortOrder order) {
		BleDeviceList result = new BleDeviceList();
		result.addAll(values());
		Collections.sort(result, new Comparator<BleDevice>() {
			@Override
			public int compare(BleDevice lhs, BleDevice rhs) {
				switch (order) {
					case ascending:
//						return lhs.getRssi() - rhs.getRssi();
						return lhs.getAverageRssi() - rhs.getAverageRssi();
					case descending:
//						return rhs.getRssi() - lhs.getRssi();
						return rhs.getAverageRssi() - lhs.getAverageRssi();
				}
				return 0;
			}
		});
		return result;
	}

	public BleDeviceList getDistanceSortedList() {
		BleDeviceList result = new BleDeviceList();
		result.addAll(values());
		Collections.sort(result, new Comparator<BleDevice>() {
			@Override
			public int compare(BleDevice lhs, BleDevice rhs) {
				double ld = lhs.getDistance();
				double rd = rhs.getDistance();
				if (ld == -1) return 1;
				if (rd == -1) return -1;
				if (ld > rd) {
					return 1;
				} else if (ld < rd) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		return result;
	}

	public void refresh() {
		for (BleDevice device : values()) {
			device.refresh();
		}
	}

}
