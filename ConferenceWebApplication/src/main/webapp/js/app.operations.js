const speakerService = "rest/";
const sessionService = "rest/";

const loadSpeakers = async () => {
    try {
        const response = await fetchWithAuth(speakerService + "speaker/all");
        if (response.status === 403) {
            showMessage("danger", "Not authorized to view speakers");
        } else if (response.status === 200) {
            const speakers = await response.json();
            updateSpeakers(speakers);
        }
    } catch (e) {
        console.error(e);
    }
};

const loadSessions = async () => {
    try {
        const response = await fetchWithAuth(sessionService + "session/all");
        if (response.status === 403) {
            showMessage("danger", "Not authorized to view sessions");
        } else if (response.status === 200) {
            const sessions = await response.json();
            const registeredIds = await loadRegistrations();
            updateSessions(sessions, registeredIds);
        }
    } catch (e) {
        console.error(e);
    }
};

const loadRegistrations = async () => {
    try {
        const response = await fetchWithAuth(sessionService + "register/current");
        var sessionIds = [];
        if (response.status === 200) {
            const data = await response.json();
            sessionIds = data.map((s) => s.id);
        }
        return sessionIds;
    } catch (e) {
        console.error(e);
    }
}

const addNewSession = async () => {
    try {
        var payload = {
            title: $("#sessionTitle").val(),
            venue: $("#sessionVenue").val(),
            date: $("#sessionDate").val(),
            duration: $("#sessionDuration").val()
        };
        const response = await fetchWithAuth(sessionService + "session/", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });
        if (response.status === 201) {
            loadSessions();
            showMessage("info", "Successfully added new session");
        }else if(response.status === 403){
            showMessage("danger", "Not authorized to create new sessions");
        }
        $("#newSessionModal").modal("hide");
    } catch (e) {
        console.error(e);
    }
}

const registerSpeaker = async () => {
    try {
        var payload = {
            name: $("#speakerName").val(),
            organization: $("#speakerOrg").val()
        };
        const response = await fetchWithAuth(speakerService + "speaker/", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });
        if (response.status === 201) {
            loadSpeakers();
            $("#show-register-speaker").attr("disabled", true);
            showMessage("info", "Successfully added yourself as a speaker");
        }else if(response.status === 403){
            showMessage("danger", "Not authorized to add yourself as a speaker");
        }
        $("#registerSpeakerModal").modal("hide");
    } catch (e) {
        console.error(e);
    }
}

const deleteSession = async (sessionId) => {
    try {
        const response = await fetchWithAuth(sessionService + `session/${sessionId}`, {
            method: "DELETE"
        });
        if (response.status === 202) {
            loadSessions();
            showMessage("info", "Session deleted succesfully");
        } else if (response.status === 403) {
            showMessage("danger", "Not authorized to delete sessions");
        }
    } catch (e) {
        console.error(e);
    }
};

const acceptSpeaker = async (speakerId) => {
    try {
        const response = await fetchWithAuth(speakerService + `speaker/accept/${speakerId}`, {
            method: "POST"
        });
        if (response.status === 202) {
            loadSpeakers();
            showMessage("info", "Speaker accepted succesfully");
        } else if (response.status === 403) {
            showMessage("danger", "Not authorized to accept speakers");
        }
    } catch (e) {
        console.error(e);
    }
};

const attendSession = async (sessionId) => {
    try {
        const response = await fetchWithAuth(sessionService + `register/${sessionId}`, {
            method: "POST"
        });
        if (response.status === 403) {
            showMessage("danger", "Not authorized");
        } else if (response.status === 200) {
            loadSessions();
            showMessage("info", "You are attending this session");
        }
    } catch (e) {
        console.error(e);
    }
};