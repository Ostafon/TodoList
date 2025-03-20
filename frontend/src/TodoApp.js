import React, { useState, useEffect } from "react";
import axios from "axios";
import "./TodoApp.css";

const API_URL = "http://localhost:8080/tasks";

const TodoApp = () => {
  const [tasks, setTasks] = useState([]);
  const [description, setDescription] = useState("");
  const [isEditing, setIsEditing] = useState(null);
  const [editText, setEditText] = useState("");

  useEffect(() => {
    fetchTasks();
  }, []);

  const fetchTasks = async () => {
    try {
      const response = await axios.get(API_URL);
      setTasks(response.data || []);
    } catch (error) {
      console.error("Ошибка загрузки задач", error);
      setTasks([]);
    }
  };

  const addTask = async () => {
    if (!description.trim()) return;
    try {
      const response = await axios.post(API_URL, { description, completed: false });
      setTasks([...tasks, response.data]);
      setDescription("");
    } catch (error) {
      console.error("Ошибка добавления задачи", error);
    }
  };

  const deleteTask = async (id) => {
    try {
      await axios.delete(`${API_URL}/${id}`);
      setTasks(tasks.filter((task) => task.id !== id));
    } catch (error) {
      console.error("Ошибка удаления задачи", error);
    }
  };

  const updateTask = async (id, updatedTask) => {
    try {
      await axios.put(`${API_URL}/${id}`, updatedTask);
      setTasks(tasks.map((task) => (task.id === id ? { ...task, ...updatedTask } : task)));
      setIsEditing(null);
    } catch (error) {
      console.error("Ошибка обновления задачи", error);
    }
  };

  return (
    <div className="container">
      <h1>📌 ToDo List</h1>
      <div className="input-container">
        <input
          type="text"
          placeholder="Введите задачу..."
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
        <button onClick={addTask}>Add</button>
      </div>

      <div className="task-list">
        {tasks.length > 0 ? (
          tasks.map((task) => (
            <div className="task-card" key={task.id}>
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
                    <button
                      className="cancel-btn"
                      onClick={() => setIsEditing(null)}
                    >
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
                  </div>
                </>
              )}
            </div>
          ))
        ) : (
          <p className="empty-text">😔 Нет задач...</p>
        )}
      </div>
    </div>
  );
};

export default TodoApp;
