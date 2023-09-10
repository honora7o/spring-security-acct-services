package accountserviceapp.business;

import accountserviceapp.persistence.EventLogRepository;
import org.springframework.stereotype.Service;

@Service
public class LoggingService {
    private final EventLogRepository eventLogRepository;

    public LoggingService(EventLogRepository eventLogRepository) {
        this.eventLogRepository = eventLogRepository;
    }

    public void logCurrEvent(EventLog eventLog) {
        eventLogRepository.save(eventLog);
    }
}
