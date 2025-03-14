/*
 * Copyright 2018 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.grpc.internal;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import io.grpc.LoadBalancer;
import io.grpc.LoadBalancerProvider;
import io.grpc.NameResolver;
import io.grpc.NameResolver.ConfigOrError;
import io.grpc.internal.PickFirstLoadBalancer.PickFirstLoadBalancerConfig;
import java.util.Map;

/**
 * Provider for the "pick_first" balancing policy.
 *
 * <p>This provides no load-balancing over the addresses from the {@link NameResolver}.  It walks
 * down the address list and sticks to the first that works.
 */
public final class PickFirstLoadBalancerProvider extends LoadBalancerProvider {
  private static final String NO_CONFIG = "no service config";
  private static final String SHUFFLE_ADDRESS_LIST_KEY = "shuffleAddressList";
  private static final String CONFIG_FLAG_NAME = "GRPC_EXPERIMENTAL_PICKFIRST_LB_CONFIG";
  @VisibleForTesting
  static boolean enablePickFirstConfig = !Strings.isNullOrEmpty(System.getenv(CONFIG_FLAG_NAME));

  @Override
  public boolean isAvailable() {
    return true;
  }

  @Override
  public int getPriority() {
    return 5;
  }

  @Override
  public String getPolicyName() {
    return "pick_first";
  }

  @Override
  public LoadBalancer newLoadBalancer(LoadBalancer.Helper helper) {
    return new PickFirstLoadBalancer(helper);
  }

  @Override
  public ConfigOrError parseLoadBalancingPolicyConfig(
      Map<String, ?> rawLoadBalancingPolicyConfig) {
    if (enablePickFirstConfig) {
      return ConfigOrError.fromConfig(
          new PickFirstLoadBalancerConfig(JsonUtil.getBoolean(rawLoadBalancingPolicyConfig,
              SHUFFLE_ADDRESS_LIST_KEY)));
    } else {
      return ConfigOrError.fromConfig(NO_CONFIG);
    }
  }
}
