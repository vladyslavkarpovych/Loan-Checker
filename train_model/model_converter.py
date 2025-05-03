import tensorflow as tf
model = tf.keras.models.load_model("loan_model.keras")
converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()
with open("loan_model.tflite", "wb") as f:
    f.write(tflite_model)