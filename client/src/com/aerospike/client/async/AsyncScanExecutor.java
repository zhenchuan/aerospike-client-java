/*
 * Aerospike Client - Java Library
 *
 * Copyright 2013 by Aerospike, Inc. All rights reserved.
 *
 * Availability of this source code to partners and customers includes
 * redistribution rights covered by individual contract. Please check your
 * contract for exact rights and responsibilities.
 */
package com.aerospike.client.async;

import com.aerospike.client.AerospikeException;
import com.aerospike.client.cluster.Node;
import com.aerospike.client.command.Command;
import com.aerospike.client.listener.RecordSequenceListener;
import com.aerospike.client.policy.ScanPolicy;

public final class AsyncScanExecutor extends AsyncMultiExecutor {
	private final RecordSequenceListener listener;

	public AsyncScanExecutor(
		AsyncCluster cluster,
		ScanPolicy policy,
		RecordSequenceListener listener,
		String namespace,
		String setName
	) throws AerospikeException {
		this.listener = listener;
		
		Command command = new Command();
		command.setScan(policy, namespace, setName);

		Node[] nodes = cluster.getNodes();
		completedSize = nodes.length;

		for (Node node : nodes) {			
			AsyncScan async = new AsyncScan(this, cluster, (AsyncNode)node, listener);
			async.execute(policy, command);
		}
	}
	
	protected void onSuccess() {
		listener.onSuccess();
	}
	
	protected void onFailure(AerospikeException ae) {
		listener.onFailure(ae);
	}		
}