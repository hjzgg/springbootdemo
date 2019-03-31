

package com.hjzgg.example.springboot.cfgcenter.client;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.core.env.EnumerablePropertySource;

/**
 * A {@link EnumerablePropertySource} that has a notion of a context which is
 * the root folder in Zookeeper.
 *
 * @author Spencer Gibb
 * @since 1.0.0
 */
public abstract class AbstractZookeeperPropertySource extends EnumerablePropertySource<CuratorFramework> {

	private String context;

	public AbstractZookeeperPropertySource(String context, CuratorFramework source) {
		super(context, source);
		this.context = context;
		if (!this.context.startsWith("/")) {
			this.context = "/" + this.context;
		}
	}

	protected String sanitizeKey(String path) {
		return path.replace(this.context + "/", "").replace('/', '.');
	}

	public String getContext() {
		return this.context;
	}
}
