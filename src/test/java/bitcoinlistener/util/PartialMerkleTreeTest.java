/*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener.util;

import bitcoinlistener.datatypes.SHA256Hash;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PartialMerkleTreeTest {

	@Test
	public void test1() {
		// test case from: https://developer.bitcoin.org/examples/p2p_networking.html#parsing-a-merkleblock
		Integer flagList[] = {1,0,1,1,1,0,0,0};
		int totalTransactions = 7;
		List<String> hashesList = Arrays.asList(
				"3612262624047ee87660be1a707519a443b1c1ce3d248cbfc6c15870f6c5daa2",
				"019f5b01d4195ecbc9398fbf3c3b1fa9bb3183301d7a1fb3bd174fcfa40a2b65",
				"41ed70551dd7e841883ab8f0b16bf04176b7d1480e4f0af9f3d4c3595768d068",
				"20d2a7bc994987302e5b1ac80fc425fe25f8b63169ea78e68fbaaefa59379bbf");

		testMerkleRoot("7f16c5962e8bd963659c793ce370d95f093bc7e367117b3c30c1f8fdd0d97287", flagList, hashesList, totalTransactions);
	}

	@Test
	public void test2() {
		Integer flagList[] = { 0, 0, 0, 0, 0, 0, 0, 0, };
		List<String> hashesList = Arrays.asList("4a396eb363e5fa669e4468c99e473ed28df13accf36e93af926963a9e36a81f0");
		int totalTransactions = 4;

		PartialMerkleTree pmt = testMerkleRoot(
				"4a396eb363e5fa669e4468c99e473ed28df13accf36e93af926963a9e36a81f0", flagList,
				hashesList, totalTransactions);
		assertTrue(pmt.getMatchedTxIds().isEmpty());
	}

	@Test
	public void test3() {
		// test case from "programming bitcoin" book
		Integer flagList[] = {1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 0,
				0};

		int totalTransactions = 3519;
		List<String> hashesList = Arrays.asList(
				"ba412a0d1480e370173072c9562becffe87aa661c1e4a6dbc305d38ec5dc088a",
				"7cf92e6458aca7b32edae818f9c2c98c37e06bf72ae0ce80649a38655ee1e27d",
				"34d9421d940b16732f24b94023e9d572a7f9ab8023434a4feb532d2adfc8c2c2",
				"158785d1bd04eb99df2e86c54bc13e139862897217400def5d72c280222c4cba",
				"ee7261831e1550dbb8fa82853e9fe506fc5fda3f7b919d8fe74b6282f92763ce",
				"f8e625f977af7c8619c32a369b832bc2d051ecd9c73c51e76370ceabd4f25097",
				"c256597fa898d404ed53425de608ac6bfe426f6e2bb457f1c554866eb69dcb8d",
				"6bf6f880e9a59b3cd053e6c7060eeacaacf4dac6697dac20e4bd3f38a2ea2543",
				"d1ab7953e3430790a9f81e1c67f5b58c825acf46bd02848384eebe9af917274c",
				"dfbb1a28a5d58a23a17977def0de10d644258d9c54f886d47d293a411cb62261");

		testMerkleRoot("ef445fef2ed495c275892206ca533e7411907971013ab83e3b47bd0d692d14d4",
					   flagList,
					   hashesList, totalTransactions);

	}


	@Test
	public void test4() {
		//test case from "programming bitcoin" book
		Integer flagList[] = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1};
		List<String> hashesList = Arrays.asList(
				"9745f7173ef14ee4155722d1cbf13304339fd00d900b759c6f9d58579b5765fb",
				"5573c8ede34936c29cdfdfe743f7f5fdfbd4f54ba0705259e62f39917065cb9b",
				"82a02ecbb6623b4274dfcab82b336dc017a27136e08521091e443e62582e8f05",
				"507ccae5ed9b340363a0e6d765af148be9cb1c8766ccc922f83e4ae681658308",
				"a7a4aec28e7162e1e9ef33dfa30f0bc0526e6cf4b11a576f6c5de58593898330",
				"bb6267664bd833fd9fc82582853ab144fece26b7a8a5bf328f8a059445b59add",
				"ea6d7ac1ee77fbacee58fc717b990c4fcccf1b19af43103c090f601677fd8836",
				"457743861de496c429912558a106b810b0507975a49773228aa788df40730d41",
				"7688029288efc9e9a0011c960a6ed9e5466581abf3e3a6c26ee317461add619a",
				"b1ae7f15836cb2286cdd4e2c37bf9bb7da0a2846d06867a429f654b2e7f383c9",
				"9b74f89fa3f93e71ff2c241f32945d877281a6a50a6bf94adac002980aafe5ab",
				"b3a92b5b255019bdaf754875633c2de9fec2ab03e6b8ce669d07cb5b18804638",
				"b5c0b915312b9bdaedd2b86aa2d0f8feffc73a2d37668fd9010179261e25e263",
				"c9d52c5cb1e557b92c84c52e7c4bfbce859408bedffc8a5560fd6e35e10b8800",
				"c555bc5fc3bc096df0a0c9532f07640bfb76bfe4fc1ace214b8b228a1297a4c2",
				"f9dbfafc3af3400954975da24eb325e326960a25b87fffe23eef3e7ed2fb610e");
		int totalTransactions = hashesList.size();

		PartialMerkleTree pmt = testMerkleRoot(
				"597c4bafe3832b17cbbabe56f878f4fc2ad0f6a402cee7fa851a9cb205f87ed1",
				flagList, hashesList, totalTransactions);
	}


	private static PartialMerkleTree testMerkleRoot(String merkleRootExpected, Integer[] flagList,
													List<String> hashesList,
													int totalTransactions) {
		List<Boolean> flags = Arrays.asList(flagList).stream().map(x -> x == 1).collect(
				Collectors.toList());
		List<SHA256Hash> hashes = hashesList.stream().map(
				x -> new SHA256Hash(ByteUtil.hexStringToByteArray(x))).collect(
				Collectors.toList());

		PartialMerkleTree m = new PartialMerkleTree(hashes, totalTransactions, flags);
		m.build();

		assertEquals(merkleRootExpected, m.getMerkleRoot().getInverted().getHashAsStr());
		return m;
	}
}
