import React, { useState, useEffect } from "react";
import axios from "axios";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import "./TodoApp.css";

const API_URL = "http://localhost:8080/tasks";

const TodoApp = () => {
  const [tasks, setTasks] = useState([]);
  const [description, setDescription] = useState("");
  const [emailTo, setEmailTo] = useState("");
  const [isEditing, setIsEditing] = useState(null);
  const [editText, setEditText] = useState("");
  const [emails, setEmails] = useState([]);
  const [expandedEmailIndex, setExpandedEmailIndex] = useState(null);
  const [showToast, setShowToast] = useState(false);
  const [highlightedTaskIds, setHighlightedTaskIds] = useState([]);

  useEffect(() => {
    fetchTasks();

    const client = new Client({
      webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
      reconnectDelay: 5000,
      onConnect: () => {
        client.subscribe("/topic/tasks", (message) => {
          const updatedTasks = JSON.parse(message.body);
          setTasks(updatedTasks);
          const updatedIds = updatedTasks.map((task) => task.id);
          setHighlightedTaskIds(updatedIds);
          setShowToast(true);
          setTimeout(() => {
            setShowToast(false);
            setHighlightedTaskIds([]);
          }, 2000);
        });
      },
    });

    client.activate();
    return () => client.deactivate();
  }, []);

  const fetchTasks = async () => {
    try {
      const response = await axios.get(API_URL);
      setTasks(response.data || []);
    } catch (error) {
      console.error("Ошибка загрузки задач", error);
    }
  };

  const addTask = async () => {
    if (!description.trim()) return;
    try {
      await axios.post(API_URL, { description, completed: false });
      setDescription("");
    } catch (error) {
      console.error("Ошибка добавления задачи", error);
    }
  };

  const deleteTask = async (id) => {
    try {
      await axios.delete(`${API_URL}/${id}`);
    } catch (error) {
      console.error("Ошибка удаления задачи", error);
    }
  };

  const updateTask = async (id, updatedTask) => {
    try {
      await axios.put(`${API_URL}/${id}`, updatedTask);
      setIsEditing(null);
    } catch (error) {
      console.error("Ошибка обновления задачи", error);
    }
  };

  const sendEmail = async (task) => {
    if (!emailTo.trim()) {
      alert("Введите email получателя");
      return;
    }

    try {
      await axios.post("http://localhost:8080/email/send", {
        to: emailTo,
        subject: "Новая задача",
        text: `Описание задачи: ${task.description}`,
      });
      alert("Письмо отправлено!");
      setEmailTo("");
    } catch (error) {
      console.error("Ошибка отправки письма", error);
      alert("Ошибка отправки");
    }
  };

  const getEmailsIMAP = async () => {
    try {
      const response = await axios.get("http://localhost:8080/email/inbox/imap");
      setEmails(response.data);
      setExpandedEmailIndex(null);
    } catch (error) {
      console.error("Ошибка получения писем (IMAP)", error);
    }
  };

  const toggleEmail = (index) => {
    setExpandedEmailIndex((prev) => (prev === index ? null : index));
  };

  return (
    <div className="container">
      <h1>📌 ToDo List</h1>

      {showToast && <div className="toast">✅ Задачи обновлены!</div>}

      <div className="input-container">
        <input
          type="text"
          placeholder="Введите задачу..."
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
        <button onClick={addTask}>Add</button>
      </div>

      <div className="input-container">
        <input
          type="email"
          placeholder="Email получателя"
          value={emailTo}
          onChange={(e) => setEmailTo(e.target.value)}
        />
      </div>

      <div className="email-buttons">
        <button onClick={getEmailsIMAP}>📥 Получить письма (IMAP)</button>
      </div>

      <div className="task-list">
        {tasks.length > 0 ? (
          tasks.map((task) => (
            <div
              className={`task-card ${highlightedTaskIds.includes(task.id) ? "highlight" : ""}`}
              key={task.id}
            >
              {isEditing === task.id ? (
                <>
                  <input
                    type="text"
                    value={editText}
                    onChange={(e) => setEditText(e.target.value)}
                    autoFocus
                  />
                  <div className="edit-buttons">
                    <button
                      className="save-btn"
                      onClick={() => updateTask(task.id, { description: editText })}
                    >
                      ✔️
                    </button>
                    <button className="cancel-btn" onClick={() => setIsEditing(null)}>
                      ❌
                    </button>
                  </div>
                </>
              ) : (
                <>
                  <p className={task.completed ? "completed" : ""}>{task.description}</p>
                  <div className="actions">
                    <button onClick={() => deleteTask(task.id)} className="delete">Delete</button>
                    <button
                      onClick={() => {
                        setIsEditing(task.id);
                        setEditText(task.description);
                      }}
                      className="edit"
                    >
                      Edit
                    </button>
                    <input
                      type="checkbox"
                      checked={task.completed}
                      onChange={() => updateTask(task.id, { completed: !task.completed })}
                    />
                    <button onClick={() => sendEmail(task)} className="send">📧</button>
                  </div>
                </>
              )}
            </div>
          ))
        ) : (
          <p className="empty-text">😔 Нет задач...</p>
        )}
      </div>

      {emails.length > 0 && (
        <div className="email-list">
          <h3>📬 Входящие письма (IMAP):</h3>
          <ul>
            {emails.map((email, idx) => (
              <li key={idx} style={{ marginBottom: "10px" }}>
                <div onClick={() => toggleEmail(idx)} style={{ cursor: "pointer" }}>
                  <strong>От:</strong> {email.from} | <strong>Тема:</strong> {email.subject}{" "}
                  {expandedEmailIndex === idx ? "🔽" : "▶️"}
                </div>
                {expandedEmailIndex === idx && (
                  <pre style={{ whiteSpace: "pre-wrap", marginTop: "5px" }}>{email.body}</pre>
                )}
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
};

export default TodoApp;
