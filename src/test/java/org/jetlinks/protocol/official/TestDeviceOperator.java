package org.jetlinks.protocol.official;

import org.jetlinks.core.ProtocolSupport;
import org.jetlinks.core.Value;
import org.jetlinks.core.Values;
import org.jetlinks.core.device.*;
import org.jetlinks.core.metadata.DeviceMetadata;
import org.jetlinks.core.things.ThingMetadata;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Map;

public class TestDeviceOperator implements DeviceOperator {

    private String  deviceId;

    private String  sessionId;

    public TestDeviceOperator(String deviceId, String sessionId) {
        this.deviceId = deviceId;
        this.sessionId = sessionId;
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public Mono<String> getConnectionServerId() {
        return null;
    }

    @Override
    public Mono<String> getSessionId() {
        return Mono.just(sessionId);
    }

    @Override
    public Mono<String> getAddress() {
        return null;
    }

    @Override
    public Mono<Void> setAddress(String address) {
        return null;
    }

    @Override
    public Mono<Boolean> putState(byte state) {
        return null;
    }

    @Override
    public Mono<Byte> getState() {
        return null;
    }

    @Override
    public Mono<Byte> checkState() {
        return null;
    }

    @Override
    public Mono<Long> getOnlineTime() {
        return null;
    }

    @Override
    public Mono<Long> getOfflineTime() {
        return null;
    }

    @Override
    public Mono<Boolean> online(String serverId, String sessionId, String address) {
        return null;
    }

    @Override
    public Mono<Boolean> online(String serverId, String address, long onlineTime) {
        return null;
    }

    @Override
    public Mono<Value> getSelfConfig(String key) {
        return null;
    }

    @Override
    public Mono<Values> getSelfConfigs(Collection<String> keys) {
        return null;
    }

    @Override
    public Mono<Boolean> offline() {
        return null;
    }

    @Override
    public Mono<Boolean> disconnect() {
        return null;
    }

    @Override
    public Mono<AuthenticationResponse> authenticate(AuthenticationRequest request) {
        return null;
    }

    @Override
    public Mono<DeviceMetadata> getMetadata() {
        return null;
    }

    @Override
    public Mono<ProtocolSupport> getProtocol() {
        return null;
    }

    @Override
    public DeviceMessageSender messageSender() {
        return null;
    }

    @Override
    public Mono<Boolean> updateMetadata(String metadata) {
        return null;
    }

    @Override
    public Mono<Boolean> updateMetadata(ThingMetadata metadata) {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Mono<Void> resetMetadata() {
        return null;
    }

    @Override
    public Mono<DeviceProductOperator> getProduct() {
        return null;
    }

    @Override
    public Mono<Value> getConfig(String key) {
        return null;
    }

    @Override
    public Mono<Values> getConfigs(Collection<String> keys) {
        return null;
    }

    @Override
    public Mono<Boolean> setConfig(String key, Object value) {
        return null;
    }

    @Override
    public Mono<Boolean> setConfigs(Map<String, Object> conf) {
        return null;
    }

    @Override
    public Mono<Boolean> removeConfig(String key) {
        return null;
    }

    @Override
    public Mono<Value> getAndRemoveConfig(String key) {
        return null;
    }

    @Override
    public Mono<Boolean> removeConfigs(Collection<String> key) {
        return null;
    }

    @Override
    public Mono<Void> refreshConfig(Collection<String> keys) {
        return null;
    }

    @Override
    public Mono<Void> refreshAllConfig() {
        return null;
    }
}
