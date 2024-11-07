# pdf_qa_backend.py
import os
import json
import logging
from flask import Flask, request, jsonify
from dotenv import load_dotenv
from langchain.document_loaders import PyPDFLoader
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain.embeddings import OpenAIEmbeddings
from langchain.vectorstores import Chroma
from langchain.chat_models import ChatOpenAI
from langchain.prompts import PromptTemplate
from langchain.schema import HumanMessage

# Load environment variables from .env file
load_dotenv()

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Set OpenAI API Key from environment variable
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
if not OPENAI_API_KEY:
    logger.error("OpenAI API key not set in environment variables.")
    raise ValueError("OpenAI API key not set in environment variables.")

# Folder containing PDF files
folder_path = os.getenv("PDF_FOLDER", 'pdfs')  # Ensure this folder exists and contains the PDFs you want to process

# Load PDFs from specified folder
def load_pdfs_from_folder(folder_path):
    all_documents = []
    for filename in os.listdir(folder_path):
        if filename.endswith(".pdf"):
            file_path = os.path.join(folder_path, filename)
            loader = PyPDFLoader(file_path)
            documents = loader.load()
            all_documents.extend(documents)
            logger.info(f"Loaded {len(documents)} documents from {filename}")
    return all_documents

# Load and process documents
all_documents = load_pdfs_from_folder(folder_path)

# Split documents into manageable chunks
text_splitter = RecursiveCharacterTextSplitter(chunk_size=1000, chunk_overlap=200)
split_docs = text_splitter.split_documents(all_documents)
logger.info(f"Total split documents: {len(split_docs)}")

# Initialize OpenAI embeddings and Chroma vector store
embeddings = OpenAIEmbeddings(model="text-embedding-ada-002", openai_api_key=OPENAI_API_KEY)
vectorstore = Chroma.from_documents(documents=split_docs, embedding=embeddings)
logger.info("Vector store initialized.")

# Initialize the Language Model
llm = ChatOpenAI(model_name="gpt-4-0125-preview", openai_api_key=OPENAI_API_KEY, max_tokens=1000, temperature=0.3)

# Define a prompt template for the assistant's response
prompt_template = """
You are an expert assistant for question-answering tasks.
Stick to answering to the point, that is, answer only the question asked by referring to the context.
Use the provided context only to answer the following question:

<context>
{context}
</context>

Question: {question}

Answer:
"""
prompt = PromptTemplate(input_variables=["context", "question"], template=prompt_template)

# Process a user query and get a response from the LLM
def process_query(user_query):
    try:
        # Step 1: Embed the user query
        query_embedding = embeddings.embed_query(user_query)

        # Step 2: Retrieve relevant documents using the query embedding
        retrieved_docs = vectorstore.similarity_search_by_vector(query_embedding, k=50)

        # Step 3: Prepare the context from retrieved documents
        context = "\n".join([doc.page_content for doc in retrieved_docs])

        # Step 4: Format the prompt with context and question
        formatted_prompt = prompt.format(context=context, question=user_query)

        # Step 5: Create a HumanMessage with the formatted prompt and get the response
        messages = [HumanMessage(content=formatted_prompt)]
        response = llm(messages)  # Adjust based on LangChain version

        logger.info(f"Processed query successfully: {user_query}")
        return response.content
    except Exception as e:
        logger.error(f"Error processing query '{user_query}': {e}", exc_info=True)
        return "I'm sorry, but I encountered an error while processing your request."

# Flask API setup
app = Flask(__name__)

# API route for querying the LLM
@app.route('/api/query', methods=['POST'])
def api_query():
    data = request.get_json()
    user_query = data.get("query")
    if not user_query:
        return jsonify({"error": "Query not provided"}), 400

    answer = process_query(user_query)
    return jsonify({"query": user_query, "answer": answer})

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5000)  # Expose the app on port 5000
