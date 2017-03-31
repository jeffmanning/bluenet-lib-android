package nl.dobots.bluenet.ble.mesh.structs;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;

import nl.dobots.bluenet.ble.cfg.BluenetConfig;
import nl.dobots.bluenet.utils.BleLog;
import nl.dobots.bluenet.utils.BleUtils;

/**
 * Copyright (c) 2015 Bart van Vliet <bart@dobots.nl>. All rights reserved.
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
 * Created on 2-3-17
 *
 * @author Bart van Vliet
 */

// Implementation of https://github.com/crownstone/bluenet/blob/master/docs/PROTOCOL.md#multi_switch_mesh_packet
public class MeshMultiSwitchPacket implements MeshPayload {

	public static final String TAG = MeshMultiSwitchPacket.class.getCanonicalName();

/* Yeah, let's not use an enum when it's this difficult */
//	public enum SwitchIntent {
//		SphereEnter(0),
//		SphereExit(1),
//		Enter(2),
//		Exit(3),
//		Manual(4),
//		Unknown(-1);
//
//		private int val;
//		SwitchIntent(int val) {
//			this.val = val;
//		}
//		public int getValue() {
//			return this.val;
//		}
//		private static final SparseArray<SwitchIntent> intToSwitchIntent = new SparseArray<>();
//		static {
//			for (SwitchIntent type : SwitchIntent.values()) {
//				intToSwitchIntent.put(type.getValue(), type);
//			}
//		}
//		public static SwitchIntent fromInt(int val) {
//			SwitchIntent intent = intToSwitchIntent.get(val);
//			if (intent == null) return SwitchIntent.Unknown;
//			return intent;
//		}
//	}


	public class MultiSwitchItem {
		private int _crownstoneId;
		private int _switchState;
		private int _timeout;
		private int _intent;

		public MultiSwitchItem(int crownstoneId, int switchState, int timeout, int intent) {
			this._crownstoneId = crownstoneId;
			this._switchState = switchState;
			this._timeout = timeout;
			this._intent = intent;
		}

		public MultiSwitchItem(ByteBuffer bb) {
			_crownstoneId =    BleUtils.toUint16(bb.getShort());
			_switchState =     BleUtils.toUint8(bb.get());
			_timeout =         BleUtils.toUint16(bb.getShort());
			_intent =          BleUtils.toUint8(bb.get());
		}

		public void toArray(ByteBuffer bb) {
			bb.putShort((short) _crownstoneId);
			bb.put((byte) _switchState);
			bb.putShort((short) _timeout);
			bb.put((byte) _intent);
		}

		public String toString() {
			return String.format(Locale.ENGLISH, "{id: %d, switch: %d, timeout: %d, intent: %d}", _crownstoneId, _switchState, _timeout, _intent);
		}
	}

	// 1B number of ids
	private static final int MULTI_SWITCH_PACKET_HEADER_SIZE = 1;
	// 2B Crownstone ID + 1B switch state + 2B timeout + 1B intent
	private static final int MULTI_SWITCH_ITEM_SIZE = 6;
	// max capacity of the list
	private static final int MAX_LIST_ELEMENTS = BluenetConfig.MESH_MAX_PAYLOAD_SIZE / MULTI_SWITCH_ITEM_SIZE;

	// number of elements in the list
	private int _size;
	// list of multi switch items
	private MultiSwitchItem[] _list = new MultiSwitchItem[MAX_LIST_ELEMENTS];


	public MeshMultiSwitchPacket() {
		_size = 0;
	}

	/**
	 * Parses the given byte array into a keep alive packet
	 * @param bytes byte array containing the keep alive packet
	 */
	public MeshMultiSwitchPacket(byte[] bytes) {
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		_size = BleUtils.toUint8(bb.get());
		if (_size > MAX_LIST_ELEMENTS) {
			BleLog.getInstance().LOGe(TAG, "Invalid length: " + _size);
			BleLog.getInstance().LOGe(TAG, "from mesh message: " + BleUtils.bytesToString(bytes));
			_size = 0;
		}

		for (int i=0; i < _size; i++) {
//			int crownstoneId =    BleUtils.toUint16(bb.getShort());
//			int switchState =     BleUtils.toUint8(bb.get());
//			int timeout =         BleUtils.toUint16(bb.getShort());
//			int intent =          BleUtils.toUint8(bb.get());
//			_list[i] = new MultiSwitchItem(crownstoneId, switchState, timeout, intent);
			_list[i] = new MultiSwitchItem(bb);
		}
	}

	/**
	 * Convert the keep alive packet into a byte array to be set as payload in a mesh control message
	 * @return byte array representation of the keep alive packet
	 */
	@Override
	public byte[] toArray() {
		ByteBuffer bb = ByteBuffer.allocate(MULTI_SWITCH_PACKET_HEADER_SIZE + _size * MULTI_SWITCH_ITEM_SIZE);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.put((byte)_size);
		for (int i = 0; i < _size; i++) {
			_list[i].toArray(bb);
		}
		return bb.array();
	}

	/**
	 * For debug purposes, create a string representation of the keep alive packet
	 * @return string representation of the object
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < _size; i++) {
			sb.append(_list[i].toString());
		}
		return String.format(Locale.ENGLISH, "{size: %d, list: [%s]}", _size, sb.toString());
	}

	/**
	 * Get the _size
	 * @return _size
	 */
	public int getSize() {
		return _size;
	}

	/**
	 * Set a new _size
	 * @param size new _size
	 */
	public void setSize(int size) {
		this._size = size;
	}

	public boolean addMultiSwitch(int crownstoneId, int switchState, int timeout, int intent) {
		if (_size + 1 < MAX_LIST_ELEMENTS) {
			_list[_size++] = new MultiSwitchItem(crownstoneId, switchState, timeout, intent);
			return true;
		} else {
			BleLog.getInstance().LOGe(TAG, "List is full");
			return false;
		}
	}

	public MultiSwitchItem getMultiSwitchItem(int index) {
		if (index < _size) {
			return _list[index];
		} else {
			BleLog.getInstance().LOGe(TAG, "index out of bounds");
			return null;
		}
	}
}