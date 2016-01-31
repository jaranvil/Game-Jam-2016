using UnityEngine;
using System.Collections;

public class Purpose : MonoBehaviour {
    // Purposes
    private string[] purposes = { "Tuesday", "Killer", "Pusher", "Potbot", "Bender", "FartLocator", "BoogieBot" };
    private string purpose;
	// Use this for initialization
	void Start () {
        choosePurpose();
	}//end start

    private string choosePurpose()
    {
        purpose = purposes[Random.Range(0, 7)];
        Debug.Log(purpose);
        return purpose;
    }//end choosePurpose

    public string getPurpose()
    {
        return purpose;
    }//end getPurpose
}//end class
