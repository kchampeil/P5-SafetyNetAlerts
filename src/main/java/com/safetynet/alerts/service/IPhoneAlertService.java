package com.safetynet.alerts.service;

import java.util.List;

public interface IPhoneAlertService {
    List<String> getPhoneAlertByFireStation(Integer stationNumber);
}
