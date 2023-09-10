package accountserviceapp.business;

import accountserviceapp.persistence.EventLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventLogService {
    private final EventLogRepository eventLogRepository;

    @Autowired
    public EventLogService(EventLogRepository eventLogRepository) {
        this.eventLogRepository = eventLogRepository;
    }

    public List<EventLog> getAllEventLogs() {
        return eventLogRepository.findAllByOrderByIdAsc();
    }
}
