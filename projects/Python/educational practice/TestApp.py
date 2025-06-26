import subprocess

def test_found():
    result = subprocess.run(
        ["python3", "App.py", "get_tpl",
         "--customer=John Smith",
         "--дата_заказа=27.05.2025",
         "--order_id=123",
         "--contact=+7 999 123 45 67"],
        capture_output=True, text=True
    )
    assert "Форма заказа" in result.stdout


def test_not_found():
    result = subprocess.run(
        ["python3", "App.py", "get_tpl", "--tumba=27.05.2025", "--yumba=+7 903 123 45 78"],
        capture_output=True, text=True
    )
    assert '"tumba": "date"' in result.stdout and '"yumba": "phone"' in result.stdout