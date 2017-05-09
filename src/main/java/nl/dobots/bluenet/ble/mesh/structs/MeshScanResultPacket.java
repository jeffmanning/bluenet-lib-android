package nl.dobots.bluenet.ble.mesh.structs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import nl.dobots.bluenet.ble.cfg.BluenetConfig;
import nl.dobots.bluenet.utils.BleUtils;

//import nl.dobots.bluenet.ble.base.structs.mesh.BleMeshHubData;

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
 * Created on 3-12-15
 *
 * @author Dominik Egger
 */
public class MeshScanResultPacket implements MeshPayload {

	public class ScannedDevice {
		private byte[] address;
		private int rssi;
		private int occurrences;

		public int getOccurrences() {
			return occurrences;
		}

		public int getRssi() {
			return rssi;
		}

		public String getAddress() {
			return BleUtils.bytesToAddress(address);
		}

//		public byte[] getAddress() {
//			return address;
//		}

		@Override
		public String toString() {
			return "ScannedDevice{" +
					"address=" + BleUtils.bytesToAddress(address) +
					", rssi=" + rssi +
					", occurrences=" + occurrences +
					'}';
		}
	}

	private static final int MESH_SCAN_RESULT_HEADER_SIZE = 1;
	private static final int SIZE_OF_DEVICE = 9;

	private int _numDevices;
//	private ScannedDevice[] devices;
	private ArrayList<ScannedDevice> _devices;

	// debug
	private Date _timeStamp;

	public Date getTimeStamp() {
		return _timeStamp;
	}


	public MeshScanResultPacket() {
		_numDevices = 0;
	}

//	/**
//	 * Parses the given byte array into a
//	 * @param bytes byte array containing the
//	 */
//	public MeshScanResultPacket(byte[] bytes) {
//		try {
//			ByteBuffer bb = ByteBuffer.wrap(bytes);
//			bb.order(ByteOrder.LITTLE_ENDIAN);
//
//			timeStamp = new Date();
//			numDevices = bb.get();
////		devices = new ScannedDevice[numDevices];
//			devices = new ArrayList<>();
//
//			for (int i = 0; i < numDevices; ++i) {
//				ScannedDevice device = new ScannedDevice();
//				// need to reverse the address because it is in little endian, and even though we
//				// set the byte order to little endian, arrays are still being read as is, and not
//				// reversed automatically
//				byte[] address = new byte[BluenetConfig.BLE_DEVICE_ADDRESS_LENGTH];
//				bb.get(address);
//				device.address = BleUtils.reverse(address);
//				device.rssi = bb.get();
//				device.occurrences = bb.getShort();
////			devices[i] = device;
//				devices.add(device);
//			}
//
////			Collections.sort(devices, new Comparator<ScannedDevice>() {
////				@Override
////				public int compare(ScannedDevice lhs, ScannedDevice rhs) {
////					return -Integer.compare(lhs.getRssi(), rhs.getRssi());
////				}
////			});
//		} catch (Exception e) {
//
//		}
//	}


	@Override
	public boolean fromArray(byte[] bytes) {
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		if (bytes.length < MESH_SCAN_RESULT_HEADER_SIZE) {
			return false;
		}

		_timeStamp = new Date();
		_numDevices = bb.get();
		if (bytes.length < MESH_SCAN_RESULT_HEADER_SIZE + _numDevices * SIZE_OF_DEVICE) {
			_numDevices = 0;
			return false;
		}

		_devices = new ArrayList<>();
		for (int i = 0; i < _numDevices; ++i) {
			ScannedDevice device = new ScannedDevice();
			// need to reverse the address because it is in little endian, and even though we
			// set the byte order to little endian, arrays are still being read as is, and not
			// reversed automatically
			byte[] address = new byte[BluenetConfig.BLE_DEVICE_ADDRESS_LENGTH];
			bb.get(address);
			device.address = BleUtils.reverse(address);
			device.rssi = bb.get();
			device.occurrences = bb.getShort();
			_devices.add(device);
		}

		return true;
	}

	/**
	 * @return byte array representation of
	 */
	public byte[] toArray() {

		ByteBuffer bb = ByteBuffer.allocate(MESH_SCAN_RESULT_HEADER_SIZE + _numDevices * SIZE_OF_DEVICE);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		bb.put((byte) _numDevices);

		for (ScannedDevice device : _devices) {
			bb.put(device.address);
			bb.put((byte) device.rssi);
			bb.putShort((short) device.occurrences);
		}

		return bb.array();
	}

	public int getNumDevices() {
		return _numDevices;
	}

	public ScannedDevice[] getDevices() {
		ScannedDevice[] array = new ScannedDevice[_devices.size()];
		_devices.toArray(array);
		return array;
	}

	@Override
	public String toString() {
		return "MeshScanResultPacket{" +
				", numDevices=" + _numDevices +
				", devices=" + Arrays.toString(getDevices()) +
				'}';
	}
}
