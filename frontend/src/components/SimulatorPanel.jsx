import React, { useState } from 'react';
import { triggerSimulatorEvent } from '../services/analyticsApi';

const SimulatorPanel = () => {
    const [campaignId, setCampaignId] = useState('CAMP_001');
    const [source, setSource] = useState('Google Ads');
    const [loading, setLoading] = useState(false);

    const simulate = async (type) => {
        setLoading(true);
        try {
            await triggerSimulatorEvent({ campaignId, source, eventType: type, amount: 50 });
            alert(`${type} event sent!`);
        } catch (err) {
            alert('Simulation failed: ' + err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="simulator-panel">
            <h3>Simulator</h3>
            <input value={campaignId} onChange={e => setCampaignId(e.target.value)} placeholder="Campaign ID" />
            <select value={source} onChange={e => setSource(e.target.value)}>
                <option>Google Ads</option>
                <option>Facebook</option>
                <option>TikTok</option>
                <option>LinkedIn</option>
            </select>
            <div className="sim-buttons">
                <button onClick={() => simulate('impression')} disabled={loading}>+100 Impressions</button>
                <button onClick={() => simulate('click')} disabled={loading}>+5 Clicks</button>
                <button onClick={() => simulate('spend')} disabled={loading}>+$50 Spend</button>
                <button onClick={() => simulate('order')} disabled={loading}>+$120 Revenue</button>
            </div>
        </div>
    );
};

export default SimulatorPanel;
